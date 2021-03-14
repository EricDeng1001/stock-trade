package org.example.trade.domain.order;

import java.time.Instant;

public final class OrderFinished extends TradeEvent {

    private final OrderStatus orderStatus;

    protected OrderFinished(Instant occurredOn, Order.Id id,
                            OrderStatus orderStatus) {
        super(occurredOn, id);
        this.orderStatus = orderStatus;
    }

    public OrderFinished(Order.Id id, OrderStatus orderStatus) {
        this(Instant.now(), id, orderStatus);
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    @Override
    public String toString() {
        return "OrderFinished{" +
            "orderStatus=" + orderStatus +
            '}';
    }

}
