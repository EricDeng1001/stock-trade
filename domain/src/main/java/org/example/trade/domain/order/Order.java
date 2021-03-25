package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSource;
import engineering.ericdeng.architecture.domain.model.annotation.AggregateRoot;
import engineering.ericdeng.architecture.domain.model.annotation.New;
import engineering.ericdeng.architecture.domain.model.annotation.Rebuild;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.request.TradeRequest;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AggregateRoot
public final class Order extends DomainEventSource<OrderEvent> {

    private final OrderId id;

    private final TradeRequest requirement;

    private final List<Trade> trades;

    private final Instant createdAt;

    private String brokerId;

    private OrderStatus orderStatus;

    private Instant submittedAt;

    private Instant closedAt;

    @Rebuild
    public Order(OrderId orderId, TradeRequest requirement, OrderStatus orderStatus,
                 List<Trade> trades, String brokerId, Instant createdAt, Instant submittedAt, Instant closedAt) {
        this.orderStatus = orderStatus;
        this.id = orderId;
        this.requirement = requirement;
        this.trades = trades;
        this.brokerId = brokerId;
        this.createdAt = createdAt;
        this.submittedAt = submittedAt;
        this.closedAt = closedAt;
    }

    @New
    public Order(AccountId account, int id, @NotNull TradeRequest requirement) {
        this(new OrderId(account, LocalDate.now(), id), requirement, OrderStatus.created,
             new ArrayList<>(4), null, Instant.now(), null, null);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant submittedAt() {
        return submittedAt;
    }

    public Instant closedAt() {
        return closedAt;
    }

    public String brokerId() {
        return brokerId;
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Shares traded() {
        Shares traded = Shares.ZERO;
        for (Trade trade : trades) {
            traded = traded.add(trade.shares());
        }
        return traded;
    }

    public void submitted(String brokerId) {
        if (submittedAt != null) { return; }
        this.brokerId = brokerId;
        orderStatus = OrderStatus.trading;
        submittedAt = Instant.now();
    }

    /**
     * finish this order so it can no longer be traded
     */
    public void close() {
        if (closedAt != null) { return; }
        int i = traded().compareTo(requirement.shares());
        closedAt = Instant.now();
        if (i == 0) {
            orderStatus = OrderStatus.fulfilled;
        } else if (i < 0) {
            orderStatus = OrderStatus.withdrawn;
        } else {
            orderStatus = OrderStatus.overflow;
        }
        raise(new OrderFinished(closedAt, id, orderStatus));
    }

    public void makeDeal(Deal deal, String brokerId) {
        Instant dealtOn = Instant.now();
        Trade trade = new Trade(brokerId, deal, dealtOn);
        trades.add(trade);
        raise(
            new OrderTraded(id, deal, dealtOn)
        );
    }

    public OrderId id() { return id; }

    public TradeRequest requirement() {
        return requirement;
    }

    public Shares unTrade() {
        return requirement.shares().subtract(traded());
    }

    public List<Trade> trades() {
        return trades;
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", request=" + requirement +
            ", trades=" + trades +
            ", orderStatus=" + orderStatus +
            ", traded=" + traded() +
            '}';
    }

    public AccountId account() {
        return id.accountId();
    }

    public OrderStatus status() {
        return orderStatus;
    }

    public boolean isTrading() {
        return orderStatus == OrderStatus.trading;
    }

}
