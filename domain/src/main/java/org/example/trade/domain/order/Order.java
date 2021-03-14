package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventPublisher;
import engineering.ericdeng.architecture.domain.model.annotation.Entity;
import engineering.ericdeng.architecture.domain.model.annotation.New;
import engineering.ericdeng.architecture.domain.model.annotation.Rebuild;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AssetLocker;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Shares;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Entity
public final class Order extends AssetLocker implements DomainEventPublisher<OrderTraded> {

    private final Id id;

    private final TradeRequest request;

    private final List<Trade> trades;

    private final Account.Id account;

    private OrderStatus orderStatus;

    private transient Shares traded;

    private transient int iterIndex;

    @Rebuild
    public Order(Account.Id account, String id, boolean signedByBroker, OrderStatus orderStatus,
                 List<Trade> trades,
                 TradeRequest request) {
        this.orderStatus = orderStatus;
        this.account = account;
        this.id = new Id(account.broker(), id, signedByBroker);
        this.request = request;
        this.trades = trades;
        traded = Shares.ZERO;
        iterIndex = 0;
    }

    @New
    public Order(Account.Id account, String brokerId, TradeRequest request) {
        this(account, brokerId, false, OrderStatus.created, new ArrayList<>(8), request);
    }

    public Shares traded() {
        ListIterator<Trade> iterator = trades.listIterator(iterIndex);
        while (iterator.hasNext()) {
            traded = traded.add(iterator.next().shares());
        }
        iterIndex = iterator.nextIndex();
        return traded;
    }

    public void startTrading() {
        if (orderStatus != OrderStatus.created) { throw new IllegalStateException("只有从未挂单的订单可以开始交易"); }
        orderStatus = OrderStatus.pending;
    }

    /**
     * finish this order so it can no longer be traded
     *
     * @param time
     */
    public void finish(Instant time) {
        int i = traded().compareTo(request.shares());
        if (i == 0) {
            orderStatus = OrderStatus.fulfilled;
        } else if (i < 0) {
            orderStatus = OrderStatus.withdrawn;
        } else {
            orderStatus = OrderStatus.overflow;
        }
        DomainEventBus.instance().publish(new OrderFinished(id, orderStatus));
    }

    public void makeDeal(Deal deal, Instant dealtOn) {
        if (orderStatus != OrderStatus.pending) {
            throw new IllegalStateException("Can't make new deal to an order which is not in pending state");
        }
        Trade trade = new Trade(id, trades.size(), deal, dealtOn);
        trades.add(trade);
        DomainEventBus.instance().publish(
            new OrderTraded(id, deal, dealtOn)
        );

    }

    public Id id() {
        return id;
    }

    public TradeRequest tradeRequest() {
        return request;
    }

    public Shares unTrade() {
        return request.shares().subtract(traded());
    }

    public List<Trade> trades() {
        return trades;
    }

    @Override
    public String toString() {
        return "TradeOrder{" +
            "id=" + id +
            ", request=" + request +
            ", trades=" + trades +
            ", account=" + account +
            ", orderStatus=" + orderStatus +
            ", traded=" + traded +
            '}';
    }

    public Account.Id account() {
        return account;
    }

    @Override
    protected String assetLockerTrackId() {
        return id.toString();
    }

    public static class Id {

        // TODO 添加交易日
        private final Broker broker;

        private String id;

        private boolean signedByBroker;

        public Id(Broker broker, String id, boolean signedByBroker) {
            this.broker = broker;
            this.id = id;
            this.signedByBroker = signedByBroker;
        }

        public String brokerId() {
            return id;
        }

        public Broker broker() {
            return broker;
        }

        public void signBrokerId(String id) {
            if (signedByBroker) {
                throw new IllegalStateException("order already be signed a broker id!");
            }
            signedByBroker = true;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Id{" +
                "broker=" + broker +
                ", idByBroker='" + id + '\'' +
                '}';
        }

    }

}
