package org.example.trade.adapter.broker.xtp;

import com.zts.xtp.common.enums.*;
import com.zts.xtp.common.jni.JNILoadLibrary;
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
import org.example.trade.interfaces.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final XTPNodeConfig XTPNodeConfig;

    private final TradeApi tradeApi;

    private XTPAccount registeredAccount;

    private volatile String sessionId;

    @Autowired
    public XTPSingleAccountSingleServiceAdapter(
        TradeService tradeService,
        SyncService syncService,
        XTPNodeConfig XTPNodeConfig
    ) {
        super(new AccountId(xtp, XTPNodeConfig.username()), syncService, tradeService);
        this.tradeService = tradeService;
        this.XTPNodeConfig = XTPNodeConfig;
        this.sessionId = "0";
        this.tradeApi = new TradeApi(this);
        JNILoadLibrary.loadLibrary();
    }

    @Override
    public boolean activate(String config) {
        if (!this.sessionId.equals("0")) { return true; }
        this.registeredAccount = new XTPAccount(supportedAccount, config);
        tradeApi.init(XTPNodeConfig.clientId(), registeredAccount.tradeKey(),
                      XTPNodeConfig.logFolder(), XtpLogLevel.XTP_LOG_LEVEL_ERROR, JniLogLevel.JNI_LOG_LEVEL_ERROR,
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
    public void submit(Order order) {
        TradeRequest requirement = order.requirement();

        OrderInsertRequest orderInsertRequest = OrderInsertRequest
            .builder()
            .orderXtpId("0") //默认0, 如果执行成功则会被覆盖为XTPId，不设置的话，会因为空指针打崩JVM
            .orderClientId(order.id().uid())
            .ticker(requirement.securityCode().code())
            .marketType(
                requirement.securityCode().market() == Market.SZ ? MarketType.XTP_MKT_SZ_A : MarketType.XTP_MKT_SH_A)
            .price(requirement.price().value().doubleValue())
            .priceType(requirement.priceType() == PriceType.LIMITED ? com.zts.xtp.common.enums.PriceType.XTP_PRICE_LIMIT
                           : com.zts.xtp.common.enums.PriceType.XTP_PRICE_BEST5_OR_CANCEL)
            .quantity(requirement.shares().value().longValue())
            .sideType(requirement.tradeSide() == TradeSide.BUY ? SideType.XTP_SIDE_BUY : SideType.XTP_SIDE_SELL)
            .businessType(BusinessType.XTP_BUSINESS_TYPE_CASH) //普通股票业务
            .positionEffectType(PositionEffectType.XTP_POSITION_EFFECT_CLOSE)
            .build();
        String s = this.tradeApi.insertOrder(orderInsertRequest, sessionId);
        if (s.equals("0")) {
            log.error("{} submit failed, reason={}", order, tradeApi.getApiLastError());
            // TODO failure recover
        } else {
            log.info("{} submitted", order);
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

    private boolean login() {
        this.sessionId =
            this.tradeApi.login(XTPNodeConfig.serverIp(), XTPNodeConfig.serverPort(),
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

    @Override
    public void onDisconnect(String sessionId, int reason) {
        log.warn("disconnected to xtp server");
        this.sessionId = null;
        login();
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
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + orderInfo.getOrderStatusType());
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
                            SecurityCode.valueOf(response.getTicker() + "." + t(response.getMarketType())),
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

    private String t(MarketType marketType) {
        switch (marketType) {
            case XTP_MKT_SH_A:
                return Market.SH.name();
            case XTP_MKT_SZ_A:
                return Market.SZ.name();
        }
        return null;
    }

    @Override
    public void onTradeEvent(TradeResponse tradeInfo, String sessionId) {
        log.info("{}, {}", tradeInfo, sessionId);
        int requestId = tradeInfo.getRequestId();
        OrderId orderId = new OrderId(registeredAccount.id(), LocalDate.now(), requestId);
        Deal deal = new Deal(
            Shares.valueOf(tradeInfo.getQuantity()),
            Price.valueOf(tradeInfo.getPrice())
        );
        tradeService.offerDeal(orderId, deal, tradeInfo.getExecId());
        log.info("order {} traded, detail={}", orderId, tradeInfo);
    }

}
