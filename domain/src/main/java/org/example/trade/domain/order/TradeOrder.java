package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventPublisher;
import engineering.ericdeng.architecture.domain.model.annotation.Entity;
import engineering.ericdeng.architecture.domain.model.annotation.New;
import engineering.ericdeng.architecture.domain.model.annotation.Rebuild;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Shares;

import java.util.ArrayList;
import java.util.List;

@Entity
public final class TradeOrder implements DomainEventPublisher<OrderTraded> {

    private final Id id;

    private final TradeRequest request;

    private final List<Trade> trades;

    private final Account.Id account;

    private OrderStatus orderStatus;

    private transient Shares traded;

    @Rebuild
    public TradeOrder(Account.Id account, String id, boolean signedByBroker, OrderStatus orderStatus,
                      List<Trade> trades,
                      TradeRequest request) {
        this.orderStatus = orderStatus;
        this.account = account;
        this.id = new Id(account.broker(), id, signedByBroker);
        this.request = request;
        this.trades = trades;
    }

    @New
    public TradeOrder(Account.Id account, String brokerId, TradeRequest request) {
        this(account, brokerId, false, OrderStatus.pending, new ArrayList<>(8), request);
    }

    public Shares traded() {
        Shares traded = Shares.ZERO;
        for (Trade r : trades) {
            traded = traded.add(r.shares());
        }
        return traded;
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

    /**
     * finish this order so it can no longer be traded
     */
    public void finish() {
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

    public void makeDeal(Deal deal) {
        if (orderStatus != OrderStatus.pending) {
            throw new IllegalStateException("Can't make new deal to an order which is not in pending state");
        }
        Trade trade = new Trade(id, trades.size(), deal);
        trades.add(trade);
        DomainEventBus.instance().publish(
            new OrderTraded(id, deal)
        );

    }

    @Override
    public String toString() {
        return "TradeOrder{" +
            "id=" + id +
            ", request=" + request +
            ", tradeResults=" + trades +
            '}';
    }

    public Account.Id account() {
        return account;
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
