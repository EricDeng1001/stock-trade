package org.example.trade.domain.order;

import org.example.trade.domain.market.Shares;

public final class Trade {

    private final Id id;

    private final Deal deal;

    @Override
    public String toString() {
        return "Trade{" +
            "id=" + id +
            ", deal=" + deal +
            '}';
    }

    public Trade(Id id, Deal deal) {
        this.id = id;
        this.deal = deal;
    }

    public Trade(TradeOrder.Id id, int index, Deal deal) {
        this(new Id(id, index), deal);
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

        private final TradeOrder.Id orderId;

        private final int index;

        public Id(TradeOrder.Id orderId, int index) {
            this.orderId = orderId;
            this.index = index;
        }

        public TradeOrder.Id orderId() {
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
