package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Shares;

import java.util.HashSet;
import java.util.Set;

public class TradeOrder {

    private final Id id;

    private final TradeRequest request;

    private final Set<OrderTraded> tradeResults;

    private final Account account;

    private OrderStatus orderStatus;

    private transient Shares traded;

    public TradeOrder(Broker broker, String brokerId, TradeRequest request,
                      OrderStatus orderStatus, Set<OrderTraded> tradeResults,
                      Account account) {
        this.orderStatus = orderStatus;
        this.account = account;
        this.id = new Id(broker, brokerId);
        this.request = request;
        this.tradeResults = tradeResults;
    }

    public TradeOrder(Broker broker, String brokerId, TradeRequest request,
                      Account account) {
        this(broker, brokerId, request, OrderStatus.pending, new HashSet<>(8), account);
    }

    public Shares traded() {
        Shares traded = Shares.ZERO;
        for (OrderTraded r : tradeResults) {
            traded = traded.add(r.deal().shares());
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

    public Set<OrderTraded> tradeResults() {
        return tradeResults;
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
    }

    public void attachTrade(OrderTraded orderTraded) {
        if (orderStatus != OrderStatus.pending) {
            throw new IllegalStateException("Can't attach trade to an order which is not in pending state");
        }
        tradeResults.add(orderTraded);
    }

    @Override
    public String toString() {
        return "TradeOrder{" +
            "id=" + id +
            ", request=" + request +
            ", tradeResults=" + tradeResults +
            '}';
    }

    public Account account() {
        return account;
    }

    public static class Id {

        private final Broker broker;

        private final String idByBroker;

        public Id(Broker broker, String idByBroker) {
            this.broker = broker;
            this.idByBroker = idByBroker;
        }

        public String brokerId() {
            return idByBroker;
        }

        public Broker broker() {
            return broker;
        }

        @Override
        public String toString() {
            return "Id{" +
                "broker=" + broker +
                ", idByBroker='" + idByBroker + '\'' +
                '}';
        }

    }

}
