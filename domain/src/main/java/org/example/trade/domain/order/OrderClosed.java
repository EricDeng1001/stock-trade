package org.example.trade.domain.order;

import java.time.Instant;

public final class OrderClosed extends OrderUpdated {

    private final OrderStatus orderStatus;

    protected OrderClosed(Instant occurredOn, OrderId id,
                          OrderStatus orderStatus) {
        super(occurredOn, id);
        this.orderStatus = orderStatus;
    }

    public OrderClosed(OrderId id, OrderStatus orderStatus) {
        this(Instant.now(), id, orderStatus);
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    @Override
    public String toString() {
        return "OrderClosed{" +
            "orderId=" + orderId +
            ", orderStatus=" + orderStatus +
            ", occurredOn=" + occurredOn +
            "} ";
    }

}
