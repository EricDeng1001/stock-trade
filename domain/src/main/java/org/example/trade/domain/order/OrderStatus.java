package org.example.trade.domain.order;

public enum OrderStatus {
    created(false),
    trading(true),
    withdrawn(false),
    overflow(false),
    fulfilled(false);

    private final boolean canTrade;

    OrderStatus(boolean canTrade) {this.canTrade = canTrade;}

    public boolean canTrade() {
        return canTrade;
    }
}
