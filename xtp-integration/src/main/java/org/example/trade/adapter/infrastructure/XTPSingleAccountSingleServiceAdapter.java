package org.example.trade.adapter.infrastructure;

import com.zts.xtp.common.enums.JniLogLevel;
import com.zts.xtp.common.enums.TransferProtocol;
import com.zts.xtp.common.enums.XtpLogLevel;
import com.zts.xtp.common.enums.XtpTeResumeType;
import com.zts.xtp.common.model.ErrorMessage;
import com.zts.xtp.trade.api.TradeApi;
import com.zts.xtp.trade.model.request.OrderInsertRequest;
import com.zts.xtp.trade.model.response.OrderResponse;
import com.zts.xtp.trade.model.response.TradeResponse;
import com.zts.xtp.trade.spi.TradeSpi;
import org.example.finance.domain.Price;
import org.example.trade.application.DealService;
import org.example.trade.application.RegisterService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.XTPAccount;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class XTPSingleAccountSingleServiceAdapter extends SingleAccountBrokerService
    implements TradeSpi {

    private static final Broker broker = Broker.valueOf("xtp");

    private static final Logger log = LoggerFactory.getLogger(XTPSingleAccountSingleServiceAdapter.class);

    private final Map<OrderId, String> idMap = new ConcurrentHashMap<>();

    private final DealService dealService;

    private final NodeConfig nodeConfig;

    private final TradeApi tradeApi;

    private XTPAccount registeredAccount;

    private volatile String sessionId;

    @Autowired
    public XTPSingleAccountSingleServiceAdapter(
        RegisterService registerService,
        DealService dealService,
        NodeConfig nodeConfig) {
        super(new AccountId(broker, nodeConfig.username()), registerService);
        this.dealService = dealService;
        this.nodeConfig = nodeConfig;
        this.tradeApi = new TradeApi(this);
    }

    @Override
    public boolean activate(String config) {
        this.registeredAccount = new XTPAccount(supportedAccount, config);
        tradeApi.init(nodeConfig.clientId(), registeredAccount.tradeKey(),
                      nodeConfig.logFolder(), XtpLogLevel.XTP_LOG_LEVEL_ERROR, JniLogLevel.JNI_LOG_LEVEL_ERROR,
                      XtpTeResumeType.XTP_TERT_QUICK);
        tradeApi.setHeartBeatInterval(180);
        if (!login()) {
            log.error("can not connected to xtp trade api, init failed, please check config");
            return false;
        }
        return true;
    }

    @Override
    public boolean deactivate() {
        return tradeApi.logout(sessionId) == 0;
    }

    @Override
    public AssetInfo queryAsset() {
        return null;
    }

    @Override
    public void submit(Order order) {
        OrderInsertRequest orderInsertRequest = new OrderInsertRequest();
        String s = this.tradeApi.insertOrder(orderInsertRequest, sessionId);
        if (s.equals("0")) {
            log.error("order {} submit failed, reason={}", order, tradeApi.getApiLastError());
        } else {
            log.info("order {} submitted", order);
            idMap.put(order.id(), s);
            dealService.orderSubmitted(order.id(), s);
        }
    }

    @Override
    public void withdraw(OrderId id) {
        String s = this.tradeApi.cancelOrder(idMap.get(id), sessionId);
        if (s.equals("0")) {
            log.error("order {} withdraw failed, reason={}", s, tradeApi.getApiLastError());
        } else {
            log.info("order {} withdrawn", s);
            dealService.finish(id);
        }
    }

    @Override
    public void onTradeEvent(TradeResponse tradeInfo, String sessionId) {
        int requestId = tradeInfo.getRequestId();
        OrderId orderId = new OrderId(registeredAccount.id(), LocalDate.now(), requestId);
        Deal deal = new Deal(
            Shares.valueOf(tradeInfo.getQuantity()),
            Price.valueOf(tradeInfo.getPrice())
        );
        dealService.newDeal(orderId, deal, tradeInfo.getExecId());
        log.info("order {} traded, detail={}", orderId, tradeInfo);
    }

    @Override
    public void onOrderEvent(OrderResponse orderInfo, ErrorMessage errorMessage, String sessionId) {
        String xtpId = orderInfo.getOrderXtpId();
        int rId = orderInfo.getRequestId();
        OrderId orderId = OrderId.valueOf(registeredAccount.id(), LocalDate.now(), rId);
        switch (orderInfo.getOrderStatusType()) {
            // 拒单
            case XTP_ORDER_STATUS_REJECTED -> {
                //29999: 深交所拒单, 10000: 上交所拒单
                log.error("order {} rejected, errorInfo={}, reason={}, detail={}",
                          orderId,
                          errorMessage,
                          this.tradeApi.getApiLastError(),
                          orderInfo);
                dealService.finish(orderId);
            }
            case XTP_ORDER_STATUS_PARTTRADEDNOTQUEUEING, XTP_ORDER_STATUS_CANCELED -> {
                log.warn("order {} traded not fully, order status={}, detail={}",
                         orderId,
                         orderInfo.getOrderStatusType().name(), orderInfo);
                dealService.finish(orderId);
            }
            case XTP_ORDER_STATUS_ALLTRADED -> dealService.finish(orderId);
            case XTP_ORDER_STATUS_UNKNOWN -> log
                .error("unknown error: {}, reason: {}, xtpId={}, detail={}", errorMessage,
                       this.tradeApi.getApiLastError(), xtpId,
                       orderInfo);
            case XTP_ORDER_STATUS_INIT -> log
                .debug("order {} is initializing now, xtpId={}, detail={} ", orderId, xtpId,
                       orderInfo);
            case XTP_ORDER_STATUS_NOTRADEQUEUEING -> log
                .debug("order {} is waiting for trade, xtpId={}, detail={}", orderId, xtpId,
                       orderInfo);
            case XTP_ORDER_STATUS_PARTTRADEDQUEUEING -> log
                .debug("order {} is executing, xtpId={}, detail={}", orderId, xtpId, orderInfo);
        }
    }

    @Override
    public void onDisconnect(String sessionId, int reason) {
        log.warn("disconnected to xtp server");
        this.sessionId = null;
        login();
    }

    private boolean login() {
        this.sessionId =
            this.tradeApi.login(nodeConfig.serverIp(), nodeConfig.serverPort(),
                                registeredAccount.username(), registeredAccount.password(),
                                TransferProtocol.XTP_PROTOCOL_TCP);
        // 0 = 失败
        if (sessionId.equals("0")) {
            this.sessionId = null;
            log.warn("connected to xtp trade api failed");
        } else {
            log.info("connected to xtp trade api");
            return true;
        }
        return false;
    }

}
