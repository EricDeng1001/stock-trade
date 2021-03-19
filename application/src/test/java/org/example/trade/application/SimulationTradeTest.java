package org.example.trade.application;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.asset.Asset;
import org.example.trade.domain.asset.AssetRepository;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.*;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.example.trade.infrastructure.BrokerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class SimulationTradeTest {

    SecurityCode securityCode = SecurityCode.valueOf("mock stock");

    Shares shares = Shares.valueOf(1000);

    Price price = new Price(10);

    TradeRequest buy = new LimitedPriceTradeRequest(
        securityCode,
        shares,
        TradeSide.BUY,
        price
    );

    TradeRequest sell = new LimitedPriceTradeRequest(
        securityCode,
        shares.divide(BigDecimal.valueOf(2)),
        TradeSide.SELL,
        price
    );

    Broker broker = new Broker("mock broker");

    Account account = new Account(broker, "mock account", "");

    ConcurrentHashMap<SecurityCode, Shares> usablePositions = new ConcurrentHashMap<>();

    Money usableCash = buy.value();

    Asset asset = new Asset(account.id(), usablePositions, new ConcurrentHashMap<>(), usableCash);

    OrderQueue orderQueue = new OrderQueue(account.id(), new ConcurrentLinkedDeque<>(), new ConcurrentLinkedDeque<>());

    AssetRepository assetRepository = new AssetRepository() {
        @Override
        public Asset findById(AccountId id) {
            return asset;
        }

        @Override
        public void save(Asset asset) {

        }
    };

    OrderQueueRepository orderQueueRepository = new OrderQueueRepository() {
        @Override
        public OrderQueue getInstance(AccountId id) {
            return orderQueue;
        }

        @Override
        public void add(OrderQueue orderQueue) {

        }
    };

    OrderRepository orderRepository = new OrderRepository() {
        final Map<OrderId, Order> map = new ConcurrentHashMap<>();

        @Override
        public Order findById(OrderId id) {
            return map.get(id);
        }

        @Override
        public void save(Order order) {
            map.put(order.id(), order);
        }

        @Override
        public int nextId() {
            return (int) System.currentTimeMillis();
        }
    };

    DealService dealService = new DealService(orderRepository);

    int simTradingWaitTime = 1000;

    int simTradeCount = 3;

    BrokerService brokerService = new BrokerService() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        final Map<OrderId, Boolean> cancels = new ConcurrentHashMap<>();

        @Override
        public void submit(Order order) {
            cancels.put(order.id(), true);
            tradeService.orderSubmitted(order.id(), UUID.randomUUID().toString());
            System.out.println("order submitted: " + order.id() + order.requirement().tradeSide());
            // broker income message
            Shares[] sims = order.requirement().shares().allocate(simTradeCount);
            for (int i = 0; i < simTradeCount; i++) {
                Shares t = sims[i];
                scheduledExecutorService.schedule(
                    () -> {
                        if (notCancel(order.id())) {
                            System.out.println("order traded: " + order.id() + order.requirement().tradeSide() + t);
                            dealService.newDeal(order.id(), new Deal(t, price), Instant.now());
                        }
                    },
                    (long) simTradingWaitTime * (i + 1),
                    TimeUnit.MILLISECONDS
                );
            }

            scheduledExecutorService.schedule(
                () -> {
                    if (notCancel(order.id())) {
                        System.out.println("order finished:" + order.id() + order.requirement().tradeSide());
                        dealService.finish(order.id(), Instant.now());
                    }
                },
                (long) simTradingWaitTime * simTradeCount + 1,
                TimeUnit.MILLISECONDS);
        }

        @Override
        public void withdraw(OrderId order) {
            System.out.println("order withdrawn: " + order);
            cancels.put(order, false);
            scheduledExecutorService.schedule(
                () -> {
                    System.out.println("order finished(withdrawn): " + order);
                    dealService.finish(order, Instant.now());
                },
                1,
                TimeUnit.MILLISECONDS);
        }

        private boolean notCancel(OrderId id) {
            return cancels.get(id);
        }
    };

    OrderService orderService = new OrderService(orderRepository, assetRepository, orderQueueRepository, brokerService);

    TradeService tradeService = new TradeService(orderRepository, brokerService);

    AssetService assetService = new AssetService(assetRepository, orderRepository);

    QueueService queueService = new QueueService(assetRepository, orderQueueRepository);

    @Test
    @DisplayName("buy then sell, with init cash")
    void tradeFlow1() throws InterruptedException {
        OrderId b = orderService.createOrder(buy, account.id());
        OrderId s = orderService.createOrder(sell, account.id());
        // waiting for user to enqueue...
        // user enqueued buy
        orderService.enqueueOrder(b);
        // waiting for trading
        Thread.sleep((long) simTradingWaitTime * simTradeCount);
        // user enqueued sell
        orderService.enqueueOrder(s);

        Thread.sleep((long) simTradingWaitTime * simTradeCount + 10);
        // user query order
        System.out.println();
        System.out.println(orderService.queryOrder(b));

        // user query order
        System.out.println();
        System.out.println(orderService.queryOrder(s));
    }

    @Test
    @DisplayName("sell then buy, with init cash")
    void tradeFlow2() throws InterruptedException {
        OrderId b = orderService.createOrder(buy, account.id());
        OrderId s = orderService.createOrder(sell, account.id());
        // waiting for user to enqueue...
        // user enqueued sell
        orderService.enqueueOrder(s);
        // waiting for trading
        Thread.sleep((long) simTradingWaitTime * simTradeCount);
        // user enqueued buy
        orderService.enqueueOrder(b);

        Thread.sleep((long) simTradingWaitTime * simTradeCount * 2);
        // user query order
        System.out.println();
        System.out.println(orderService.queryOrder(b));

        // user query order
        System.out.println();
        System.out.println(orderService.queryOrder(s));
    }

    @Test
    @DisplayName("buy then sell, with init cash, but dequeue sell")
    void tradeFlow3() throws InterruptedException {
        OrderId b = orderService.createOrder(buy, account.id());
        OrderId s = orderService.createOrder(sell, account.id());
        // waiting for user to enqueue...
        // user enqueued buy
        orderService.enqueueOrder(b);
        // waiting for trading
        Thread.sleep((long) simTradingWaitTime * simTradeCount);
        // user enqueued sell
        orderService.enqueueOrder(s);
        Thread.sleep(simTradingWaitTime);

        orderService.dequeueOrder(s);

        Thread.sleep((long) simTradingWaitTime * simTradeCount + 10);
        // user query order
        System.out.println();
        System.out.println(orderService.queryOrder(b));

        // user query order
        System.out.println();
        System.out.println(orderService.queryOrder(s));
    }

}
