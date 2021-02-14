package org.example.trade.domain.trade;

import java.time.Instant;

public class OrderTraded extends TradeEvent {

    private final Deal deal;

    public OrderTraded(TradeOrder.Id id, String idByBroker, Deal deal, Instant occurredOn) {
        super(occurredOn, id, idByBroker);
        this.deal = deal;
    }

    public OrderTraded(TradeOrder.Id id, String idByBroker, Deal deal) {
        super(Instant.now(), id, idByBroker);
        this.deal = deal;
    }

    public Deal deal() {
        return deal;
    }

    @Override
    public String toString() {
        return "OrderTraded{" +
            "deal=" + deal +
            '}';
    }

}
