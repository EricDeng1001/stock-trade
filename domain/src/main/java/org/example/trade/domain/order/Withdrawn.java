package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.DomainEvent;

import java.time.Instant;

public class Withdrawn extends DomainEvent {

    protected Withdrawn(Instant occurredOn) {
        super(occurredOn);
    }

}