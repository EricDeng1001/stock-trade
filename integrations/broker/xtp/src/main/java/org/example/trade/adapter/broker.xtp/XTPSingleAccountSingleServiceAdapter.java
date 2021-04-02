package org.example.trade.adapter.broker.xtp;

import com.zts.xtp.common.enums.*;
import com.zts.xtp.common.model.ErrorMessage;
import com.zts.xtp.trade.api.TradeApi;
import com.zts.xtp.trade.model.request.OrderInsertRequest;
import com.zts.xtp.trade.model.response.AssetResponse;
import com.zts.xtp.trade.model.response.OrderResponse;
import com.zts.xtp.trade.model.response.StockPositionResponse;
import com.zts.xtp.trade.model.response.TradeResponse;
import com.zts.xtp.trade.spi.TradeSpi;
import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.adapter.broker.SingleAccountBrokerService;
import org.example.trade.application.RegisterService;
import org.example.trade.application.SyncService;
import org.example.trade.application.TradeService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.XTPAccount;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Market;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.*;
import org.example.trade.domain.order.request.TradeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Profile("xtp")
@Component
public class XTPSingleAccountSingleServiceAdapter
    extends SingleAccountBrokerService implements TradeSpi {

    private static final Broker xtp = Broker.valueOf("xtp");

    private static final Logger log = LoggerFactory.getLogger(XTPSingleAccountSingleServiceAdapter.class);

    private final Map<OrderId, String> idMap = new ConcurrentHashMap<>();

    private final AtomicInteger requestId = new AtomicInteger(0);

    private final Map<Integer, AssetResponse> assetResponseMap = new HashMap<>();

    private final Map<Integer, ConcurrentLinkedQueue<StockPositionResponse>> positionResponsesMap =
        new HashMap<>();

    private final Map<Integer, CountDownLatch> latchMap = new HashMap<>();

    private final TradeService tradeService;

    private final NodeConfig nodeConfig;

    private final TradeApi tradeApi;

    private XTPAccount registeredAccount;

    private volatile String sessionId;

    @Autowired
    public XTPSingleAccountSingleServiceAdapter(
        RegisterService registerService,
        TradeService tradeService,
        SyncService syncService,
        NodeConfig nodeConfig
    ) {
        super(new AccountId(xtp, nodeConfig.username()), registerService, syncService, tradeService);
        this.tradeService = tradeService;
        this.nodeConfig = nodeConfig;
        this.sessionId = "0";
        this.tradeApi = new TradeApi(this);
    }

    @Override
    public boolean activate(String config) {
        if (!this.sessionId.equals("0")) { return true; }
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
        if (this.sessionId.equals("0")) { return true; }
        int logout = tradeApi.logout(sessionId);
        if (logout == 0) {
            this.sessionId = "0";
            return true;
        }
        return false;
    }

    @Override
    public void queryAsset() {
        int requestId = this.requestId.incrementAndGet();
        ConcurrentLinkedQueue<StockPositionResponse> positionResponses = new ConcurrentLinkedQueue<>();
        positionResponsesMap.put(requestId, positionResponses);
        latchMap.put(requestId, new CountDownLatch(1));
        try {
            this.tradeApi.queryAsset(sessionId, requestId);
            this.tradeApi.queryPosition("", sessionId, requestId);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void onQueryAsset(AssetResponse assetInfo, ErrorMessage errorMessage, String sessionId) {
        if (errorMessage.getErrorId() != 0) {
            log.error("synchronization balances failed: {}", tradeApi.getApiLastError());
        } else {
            int requestId = assetInfo.getRequestId();
            assetResponseMap.put(requestId, assetInfo);
            if (assetInfo.isLastResp()) {
                latchMap.get(requestId).countDown();
            }
        }
    }

    @Override
    public void onQueryPosition(StockPositionResponse stockPositionInfo, ErrorMessage errorMessage, String sessionId) {
        if (errorMessage.getErrorId() != 0) {
            log.error("synchronization positions failed: {}", tradeApi.getApiLastError());
        } else {
            int requestId = stockPositionInfo.getRequestId();
            ConcurrentLinkedQueue<StockPositionResponse> responses = positionResponsesMap.get(requestId);
            responses.add(stockPositionInfo);
            if (stockPositionInfo.isLastResp()) {
                try {
                    latchMap.get(requestId).await();
                    AssetResponse assetResponse = assetResponseMap.get(requestId);
                    Money usableCash = Money.valueOf(assetResponse.getTotalAsset());
                    Map<SecurityCode, Shares> usablePositions = new HashMap<>(responses.size());
                    for (StockPositionResponse response : responses) {
                        usablePositions.put(
                            SecurityCode.valueOf(response.getTicker()),
                            Shares.valueOf(response.getTotalQty())
                        );
                    }
                    AssetInfo assetInfo = new AssetInfo(usablePositions, usableCash);
                    syncService.syncAsset(supportedAccount, assetInfo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void submit(Order order) {
        TradeRequest requirement = order.requirement();
        OrderInsertRequest orderInsertRequest = new OrderInsertRequest();
        orderInsertRequest.setOrderClientId(order.id().uid());
        orderInsertRequest.setMarketType(
            requirement.securityCode().market() == Market.SZ ? MarketType.XTP_MKT_SZ_A : MarketType.XTP_MKT_SH_A);
        orderInsertRequest.setPrice(requirement.price().value().doubleValue());
        orderInsertRequest.setPriceType(
            requirement.priceType() == PriceType.LIMITED ? com.zts.xtp.common.enums.PriceType.XTP_PRICE_LIMIT
                : com.zts.xtp.common.enums.PriceType.XTP_PRICE_BEST5_OR_CANCEL);
        orderInsertRequest.setSideType(requirement.tradeSide() == TradeSide.BUY ? SideType.XTP_SIDE_BUY : SideType.XTP_SIDE_SELL);
        orderInsertRequest.setQuantity(requirement.shares().value().longValue());
        orderInsertRequest.setOrderXtpId("0");
        String s = this.tradeApi.insertOrder(orderInsertRequest, sessionId);
        if (s.equals("0")) {
            log.error("order {} submit failed, reason={}", order, tradeApi.getApiLastError());
            // TODO failure recover
        } else {
            log.info("order {} submitted", order);
            idMap.put(order.id(), s);
        }
    }

    @Override
    public void withdraw(OrderId id) {
        String s = this.tradeApi.cancelOrder(idMap.get(id), sessionId);
        if (s.equals("0")) {
            log.error("order {} withdraw failed, reason={}", s, tradeApi.getApiLastError());
            // TODO failure recover
        } else {
            log.info("order {} withdrawn", s);
            tradeService.closeOrder(id);
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
        tradeService.offerDeal(orderId, deal, tradeInfo.getExecId());
        log.info("order {} traded, detail={}", orderId, tradeInfo);
    }

    @Override
    public void onOrderEvent(OrderResponse orderInfo, ErrorMessage errorMessage, String sessionId) {
        String xtpId = orderInfo.getOrderXtpId();
        int rId = orderInfo.getRequestId();
        OrderId orderId = OrderId.valueOf(registeredAccount.id(), LocalDate.now(), rId);
        switch (orderInfo.getOrderStatusType()) {
            // 拒单
            case XTP_ORDER_STATUS_REJECTED:
                //29999: 深交所拒单, 10000: 上交所拒单
                log.error("order {} rejected, errorInfo={}, reason={}, detail={}",
                          orderId,
                          errorMessage,
                          this.tradeApi.getApiLastError(),
                          orderInfo);
                tradeService.closeOrder(orderId);
                break;
            case XTP_ORDER_STATUS_PARTTRADEDNOTQUEUEING:
            case XTP_ORDER_STATUS_CANCELED:
                log.warn("order {} traded not fully, order status={}, detail={}",
                         orderId,
                         orderInfo.getOrderStatusType().name(), orderInfo);
                tradeService.closeOrder(orderId);
                break;
            case XTP_ORDER_STATUS_ALLTRADED:
                tradeService.closeOrder(orderId);
                break;
            case XTP_ORDER_STATUS_UNKNOWN:
                log
                    .error("unknown error: {}, reason: {}, xtpId={}, detail={}", errorMessage,
                           this.tradeApi.getApiLastError(), xtpId,
                           orderInfo);
                break;
            case XTP_ORDER_STATUS_INIT:
                log
                    .debug("order {} is initializing now, xtpId={}, detail={} ", orderId, xtpId,
                           orderInfo);
                tradeService.startTradingOrder(orderId, xtpId);
                break;
            case XTP_ORDER_STATUS_NOTRADEQUEUEING:
                log
                    .debug("order {} is waiting for trade, xtpId={}, detail={}", orderId, xtpId,
                           orderInfo);
                break;
            case XTP_ORDER_STATUS_PARTTRADEDQUEUEING:
                log
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
