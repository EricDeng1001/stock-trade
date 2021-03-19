package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEvent;

import java.time.Instant;

public abstract class OrderEvent extends DomainEvent {

    protected final OrderId orderId;

    protected OrderEvent(Instant occurredOn, OrderId id) {
        super(occurredOn);
        this.orderId = id;
    }

    public OrderId orderId() {
        return orderId;
    }

}
