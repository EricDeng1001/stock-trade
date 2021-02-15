package org.example.trade.domain.tradeorder;

import java.time.Instant;

public final class OrderTraded extends TradeEvent {

    private final Deal deal;

    public OrderTraded(TradeOrder.Id id, Deal deal, Instant occurredOn) {
        super(occurredOn, id);
        this.deal = deal;
    }

    public OrderTraded(TradeOrder.Id id, Deal deal) {
        this(id, deal, Instant.now());
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
