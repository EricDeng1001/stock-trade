package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEvent;

import java.time.Instant;

public class TradeEvent extends DomainEvent {

    private final Order.Id orderId;

    protected TradeEvent(Instant occurredOn, Order.Id id) {
        super(occurredOn);
        this.orderId = id;
    }

    public Order.Id orderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "TradeEvent{" +
            "orderId=" + orderId +
            "} " + super.toString();
    }

}
