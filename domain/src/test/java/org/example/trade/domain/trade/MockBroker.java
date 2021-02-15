package org.example.trade.domain.trade;

import org.example.trade.domain.market.*;
import org.example.trade.domain.order.*;
import org.example.trade.infrastructure.broker.BrokerCallbackHandler;
import org.example.trade.infrastructure.broker.BrokerService;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class MockBroker extends Broker implements BrokerService, TradeOrderRepository, MarketInfoService {

    private static final AtomicLong i = new AtomicLong(0);

    private final Map<TradeOrder.Id, TradeOrder> orderMap = new ConcurrentHashMap<>();

    private final Map<StockCode, Stock> stockMap = new ConcurrentHashMap<>();

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Queue<BrokerCallbackHandler> brokerCallbackHandlers = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    private final Semaphore scheduledTask = new Semaphore(2000);

    public MockBroker() {
        super("mock broker");
    }

    @Override
    public TradeOrder trade(TradeRequest tradeRequest) {
        scheduledTask.acquireUninterruptibly();
        TradeOrder o = new TradeOrder(this, String.valueOf(i.getAndIncrement()), tradeRequest, tradeRequest.account());
        orderMap.put(o.id(), o);
        mockTrading(o);
        return o;
    }

    @Override
    public void register(BrokerCallbackHandler brokerCallbackHandler) {
        brokerCallbackHandlers.add(brokerCallbackHandler);
    }

    @Override
    public TradeOrder findById(TradeOrder.Id id) {
        return orderMap.get(id);
    }

    @Override
    public void save(TradeOrder order) {
        orderMap.put(order.id(), order);
    }

    @Override
    public Stock queryStock(StockCode stockCode) {
        return stockMap
            .computeIfAbsent(stockCode,
                             stockCode1 ->
                                 new Stock(
                                     stockCode1,
                                     new Price(random.nextInt()),
                                     new Price(random.nextInt()),
                                     new Price(random.nextInt()),
                                     new FiveLevelPrice(
                                         new Price[]{
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt())
                                         },
                                         new Price[]{
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt()),
                                             new Price(random.nextInt())
                                         }
                                     ),
                                     new Shares(random.nextInt())));
    }

    public void shutdown() {
        scheduledTask.acquireUninterruptibly(2000);
    }

    private void mockTrading(TradeOrder o) {
        scheduledExecutorService.schedule(() -> {
            for (BrokerCallbackHandler h : brokerCallbackHandlers) {
                Shares unTrade = o.unTrade();
                Shares mockTrade = randomTake(unTrade);
                OrderTraded orderTraded = new OrderTraded(o.id(), UUID.randomUUID().toString(),
                                                          new Deal(mockTrade,
                                                                   queryStock(o.tradeRequest().stockCode())
                                                                       .currentPrice()));
                h.onTrade(orderTraded);
                if (!mockTrade.equals(unTrade)) {
                    scheduledTask.acquireUninterruptibly();
                    mockTrading(o);
                }
                scheduledTask.release();
            }
        }, random.nextLong(10, 1000), TimeUnit.MILLISECONDS);
    }

    private Shares randomTake(Shares unTrade) {
        int bound = unTrade.value().intValue();
        int x;
        if (bound < 100) {
            x = bound;
        } else {
            x = random.nextInt(RegularizedShares.RS100.value().intValue(),
                               bound);
        }
        return new Shares(x);
    }

}
