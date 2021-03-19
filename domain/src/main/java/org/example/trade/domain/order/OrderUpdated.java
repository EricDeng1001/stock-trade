package org.example.trade.domain.order;

import java.time.Instant;

public abstract class OrderUpdated extends OrderEvent {

    protected OrderUpdated(Instant occurredOn, OrderId id) {
        super(occurredOn, id);
    }

}
