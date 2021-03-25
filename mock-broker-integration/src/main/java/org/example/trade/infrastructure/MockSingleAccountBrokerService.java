package org.example.trade.infrastructure;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.application.DealService;
import org.example.trade.application.RegisterService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class MockSingleAccountBrokerService extends SingleAccountBrokerService {

    private static final int simTradingWaitTime = 1000;

    private static final String[] mockStocks = new String[]{
        "stock 1",
        "stock 2",
        "stock 3",
        "stock 4",
        "stock 5",
        "stock 6"
    };

    private static final Broker broker = Broker.valueOf("mock broker");

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final Map<OrderId, Boolean> cancels = new ConcurrentHashMap<>();

    private final DealService dealService;

    @Autowired
    public MockSingleAccountBrokerService(
        DealService dealService,
        RegisterService registerService,
        MockConfig config
    ) {
        super(new AccountId(broker, config.getUsername()), registerService);
        this.dealService = dealService;
    }

    @Override
    public boolean activate(String config) {
        return true;
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    @Override
    public void submitImpl(Order order) {
        cancels.put(order.id(), true);
        TradeRequest requirement = order.requirement();
        dealService.orderSubmitted(order.id(), UUID.randomUUID().toString());
        // broker income message
        int simTradeCount = ThreadLocalRandom.current().nextInt(2, 6);

        Shares[] sims = requirement.shares().allocate(simTradeCount);
        for (int i = 0; i < simTradeCount; i++) {
            Shares t = sims[i];
            scheduledExecutorService.schedule(
                () -> {
                    if (notCancel(order.id())) {
                        dealService.newDeal(
                            order.id(),
                            new Deal(
                                t,
                                switch (requirement.priceType()) {
                                    case MARKET -> Price.TEN;
                                    case LIMITED -> ((LimitedPriceTradeRequest) requirement).targetPrice();
                                }),
                            UUID.randomUUID().toString());
                    }
                },
                (long) simTradingWaitTime * (i + 1),
                TimeUnit.MILLISECONDS
            );
        }

        scheduledExecutorService.schedule(
            () -> {
                if (notCancel(order.id())) {
                    dealService.finish(order.id());
                }
            },
            (long) simTradingWaitTime * simTradeCount + 1,
            TimeUnit.MILLISECONDS);
    }

    @Override
    public void withdrawImpl(OrderId order) {
        cancels.put(order, false);
        scheduledExecutorService.schedule(
            () -> {
                dealService.finish(order);
            },
            1,
            TimeUnit.MILLISECONDS);
    }

    @Override
    protected AssetInfo queryAssetImpl() {
        ConcurrentHashMap<SecurityCode, Shares> usablePositions = new ConcurrentHashMap<>();
        for (String s : mockStocks) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                usablePositions.put(SecurityCode.valueOf(s),
                                    Shares.valueOf(ThreadLocalRandom.current().nextInt(0, 5)));
            }
        }
        Money usableCash = Money.valueOf(ThreadLocalRandom.current().nextInt(100, 400));
        return new AssetInfo(usablePositions, usableCash);
    }

    private boolean notCancel(OrderId id) {
        return cancels.get(id);
    }

}
