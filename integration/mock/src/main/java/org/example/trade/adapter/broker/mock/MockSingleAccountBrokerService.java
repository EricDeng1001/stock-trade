package org.example.trade.adapter.broker.mock;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.application.TradeService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.interfaces.SyncService;
import org.example.trade.port.broker.SingleAccountBrokerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class MockSingleAccountBrokerService extends SingleAccountBrokerService {

    private static final int simTradingWaitTime = 10;

    private static final String[] mockStocks = new String[]{
        "000001.SZ",
        "000002.SZ",
        "000003.SZ",
        "000004.SZ",
        "000005.SZ",
        "000006.SZ"
    };

    private static final Broker broker = Broker.valueOf("mock broker");

    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<OrderId, Boolean> cancels = new ConcurrentHashMap<>();

    public MockSingleAccountBrokerService(
        TradeService tradeService,
        SyncService syncService,
        UserConfig config,
        ScheduledExecutorService scheduledExecutorService) {
        super(new AccountId(broker, config.getUsername()), syncService, tradeService);
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public boolean connect(String config) {
        return true;
    }

    @Override
    public boolean disconnect() {
        return true;
    }

    @Override
    public void queryAsset() {
        ConcurrentHashMap<SecurityCode, Shares> usablePositions = new ConcurrentHashMap<>();
        for (String s : mockStocks) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                usablePositions.put(SecurityCode.valueOf(s),
                                    Shares.valueOf(ThreadLocalRandom.current().nextInt(0, 5)));
            }
        }
        usablePositions.put(SecurityCode.valueOf("000001.SZ"),
                            Shares.valueOf(ThreadLocalRandom.current().nextInt(10, 20)));
        Money usableCash = Money.valueOf(ThreadLocalRandom.current().nextInt(200, 400));
        syncService.syncAsset(supportedAccount, new AssetInfo(usablePositions, usableCash));
    }

    @Override
    public void submit(Order order) {
        cancels.put(order.id(), true);
        TradeRequest requirement = order.requirement();
        tradeService.startTradingOrder(order.id(), UUID.randomUUID().toString());
        // broker income message
        int simTradeCount = ThreadLocalRandom.current().nextInt(2, 5);
        Shares[] sims = requirement.shares().allocate(simTradeCount);
        Semaphore semaphore = new Semaphore(-simTradeCount + 1);
        for (int i = 0; i < simTradeCount; i++) {
            Shares t = sims[i];
            int k = i;
            scheduledExecutorService.schedule(
                () -> {
                    if (notCancel(order.id())) {
                        tradeService.offerDeal(
                            order.id(),
                            new Deal(
                                t,
                                requirement.priceType() == PriceType.MARKET ? Price.TEN
                                    : ((LimitedPriceTradeRequest) requirement).targetPrice()
                            ),
                            UUID.randomUUID().toString());
                        semaphore.release(1);
                        // all deal offered, then close the order, as promised
                        if (k == simTradeCount - 1) {
                            semaphore.acquireUninterruptibly(1);
                            tradeService.closeOrder(order.id());
                        }
                    }
                },
                (long) simTradingWaitTime * (i + 1),
                TimeUnit.MILLISECONDS
            );
        }

    }

    @Override
    public void withdraw(OrderId order) {
        cancels.put(order, false);
        scheduledExecutorService.schedule(
            () -> {
                synchronized (order) {
                    tradeService.closeOrder(order);
                }
            },
            1,
            TimeUnit.MILLISECONDS);
    }

    private boolean notCancel(OrderId id) {
        return cancels.get(id);
    }

}
