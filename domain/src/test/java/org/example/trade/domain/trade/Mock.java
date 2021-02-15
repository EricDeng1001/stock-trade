package org.example.trade.domain.trade;

import org.example.trade.domain.market.*;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.TradeOrder;
import org.example.trade.domain.order.TradeOrderRepository;
import org.example.trade.domain.order.TradeService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class Mock extends TradeService implements TradeOrderRepository, MarketInfoService {

    private static final AtomicLong i = new AtomicLong(0);

    private final Broker broker = new Broker("mock broker");

    private final Map<TradeOrder.Id, TradeOrder> orderMap = new ConcurrentHashMap<>();

    private final Map<StockCode, Stock> stockMap = new ConcurrentHashMap<>();

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    private final Semaphore scheduledTask = new Semaphore(2000);

    @Override
    public boolean applyOrder(TradeOrder o) {
        scheduledTask.acquireUninterruptibly();
        orderMap.put(o.id(), o);
        mockTrading(o);
        o.id().signBrokerId(UUID.randomUUID().toString());
        return true;
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

    public Broker broker() {
        return broker;
    }

    private void mockTrading(TradeOrder o) {
        scheduledExecutorService.schedule(() -> {
            Shares unTrade = o.unTrade();
            Shares mockTrade = randomTake(unTrade);
            o.makeDeal(new Deal(mockTrade, queryStock(o.tradeRequest().stockCode()).currentPrice()));
            if (!mockTrade.equals(unTrade)) {
                scheduledTask.acquireUninterruptibly();
                mockTrading(o);
            }
            scheduledTask.release();
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
