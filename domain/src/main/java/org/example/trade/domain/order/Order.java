package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventPublisher;
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
public final class Order implements DomainEventPublisher<OrderTraded> {

    private final OrderId id;

    private final TradeRequest requirement;

    private final List<Trade> trades;

    private String brokerId;

    private OrderStatus orderStatus;

    @Rebuild
    public Order(OrderId orderId, TradeRequest requirement, OrderStatus orderStatus,
                 List<Trade> trades) {
        this.orderStatus = orderStatus;
        this.id = orderId;
        this.requirement = requirement;
        this.trades = trades;
    }

    @New
    public Order(AccountId account, int id, @NotNull TradeRequest requirement) {
        this(new OrderId(account, LocalDate.now(), id), requirement, OrderStatus.created,
             new ArrayList<>(4));
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

    public void trading(String brokerId) {
        this.brokerId = brokerId;
        orderStatus = OrderStatus.trading;
    }

    /**
     * finish this order so it can no longer be traded
     *
     * @param time closed time
     */
    public void close(Instant time) {
        int i = traded().compareTo(requirement.shares());
        if (i == 0) {
            orderStatus = OrderStatus.fulfilled;
        } else if (i < 0) {
            orderStatus = OrderStatus.withdrawn;
        } else {
            orderStatus = OrderStatus.overflow;
        }
        DomainEventBus.instance().publish(new OrderFinished(time, id, orderStatus));
    }

    public void makeDeal(Deal deal, Instant dealtOn) {
        Trade trade = new Trade(id, trades.size(), deal, dealtOn);
        trades.add(trade);
        DomainEventBus.instance().publish(
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
