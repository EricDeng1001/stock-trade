package org.example.trade.domain.trade;

import org.example.finance.domain.Price;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.market.*;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderFactory;
import org.example.trade.domain.order.OrderRepository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class MockTradeService extends OrderFactory implements MarketInfoService {

    private static final AtomicLong i = new AtomicLong(0);

    private final Broker broker = new Broker("mock broker");

    private final Map<SecurityCode, Stock> stockMap = new ConcurrentHashMap<>();

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    private final Semaphore scheduledTask = new Semaphore(2000);

    public MockTradeService(OrderRepository orderRepository,
                            AccountRepository accountRepository) {
        super(orderRepository, accountRepository);
    }

    @Override
    public void startTrade(Order o) {
        scheduledTask.acquireUninterruptibly();
        orderRepository.save(o);
        mockTrading(o);
        o.id().signBrokerId(UUID.randomUUID().toString());
    }

    @Override
    public Stock queryStock(SecurityCode securityCode) {
        return stockMap
            .computeIfAbsent(securityCode,
                             stockCode1 ->
                                 new Stock(
                                     stockCode1,
                                     new Price("2.2"),
                                     new Price("2.2"),
                                     new Price("2.2"),
                                     new FiveLevelPrice(
                                         new Price[]{
                                             new Price("2.0"),
                                             new Price("1.5"),
                                             new Price("1.2"),
                                             new Price("1.0"),
                                             new Price("0.5")
                                         },
                                         new Price[]{
                                             new Price("2.5"),
                                             new Price("3.0"),
                                             new Price("3.5"),
                                             new Price("4.0"),
                                             new Price("5.0")
                                         }
                                     ),
                                     new Shares(30000000)));
    }

    public void shutdown() {
        scheduledTask.acquireUninterruptibly(2000);
    }

    public Broker broker() {
        return broker;
    }

    private void mockTrading(Order o) {
        scheduledExecutorService.schedule(() -> {
            Shares unTrade = o.unTrade();
            Shares mockTrade = randomTake(unTrade);
            o.makeDeal(new Deal(mockTrade, queryStock(o.tradeRequest().securityCode()).currentPrice()), Instant.now());
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
            x = random.nextInt(100, bound);
        }
        return new Shares(x);
    }

}
