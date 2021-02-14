package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEvent;

import java.time.Instant;

public class TradeEvent extends DomainEvent {

    private final Id id;

    protected TradeEvent(Instant occurredOn, TradeOrder.Id id, String idByBroker) {
        super(occurredOn);
        this.id = new Id(id, idByBroker);
    }

    public TradeOrder.Id orderId() {
        return id.orderId;
    }

    public Id id() {
        return id;
    }

    public static class Id {

        private final TradeOrder.Id orderId;

        private final String idByBroker;

        public Id(TradeOrder.Id orderId, String idByBroker) {
            this.orderId = orderId;
            this.idByBroker = idByBroker;
        }

        public TradeOrder.Id orderId() {
            return orderId;
        }

        public String idByBroker() {
            return idByBroker;
        }

        @Override
        public String toString() {
            return "Id{" +
                "orderId=" + orderId +
                ", idByBroker='" + idByBroker + '\'' +
                '}';
        }

    }

}
