package org.example.trade.domain.order;

import java.time.Instant;

public class OrderStatusChanged extends OrderEvent {

    private final OrderStatus orderStatus;

    public OrderStatusChanged(Instant occurredOn, OrderId id, OrderStatus orderStatus) {
        super(occurredOn, id);
        this.orderStatus = orderStatus;
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

}
