package org.example.trade.domain.order;

import java.time.Instant;

public final class OrderTraded extends OrderUpdated {

    private final Deal deal;

    public OrderTraded(OrderId id, Deal deal, Instant occurredOn) {
        super(occurredOn, id);
        this.deal = deal;
    }

    public OrderTraded(OrderId id, Deal deal) {
        this(id, deal, Instant.now());
    }

    public Deal deal() {
        return deal;
    }

    @Override
    public String toString() {

        return "OrderTraded{" +
            "orderId=" + orderId +
            ", deal=" + deal +
            ", occurredOn=" + occurredOn +
            '}';
    }

}
