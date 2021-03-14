package org.example.trade.domain.order;

import org.example.trade.domain.market.Shares;

import java.time.Instant;

public final class Trade {

    private final Id id;

    private final Deal deal;

    private final Instant dealtOn;

    public Trade(Id id, Deal deal, Instant dealtOn) {
        this.id = id;
        this.deal = deal;
        this.dealtOn = dealtOn;
    }

    public Trade(Order.Id id, int index, Deal deal, Instant dealtOn) {
        this(new Id(id, index), deal, dealtOn);
    }

    public Instant dealtOn() {
        return dealtOn;
    }

    @Override
    public String toString() {
        return "Trade{" +
            "id=" + id +
            ", deal=" + deal +
            '}';
    }

    public Id id() {
        return id;
    }

    public Deal deal() {
        return deal;
    }

    public Shares shares() {
        return deal.shares();
    }

    public static class Id {

        private final Order.Id orderId;

        private final int index;

        public Id(Order.Id orderId, int index) {
            this.orderId = orderId;
            this.index = index;
        }

        public Order.Id orderId() {
            return orderId;
        }

        public int idByBroker() {
            return index;
        }

        @Override
        public String toString() {
            return "Id{" +
                "orderId=" + orderId +
                ", index=" + index +
                '}';
        }

    }

}
