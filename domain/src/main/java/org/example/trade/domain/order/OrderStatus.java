package org.example.trade.domain.order;

public enum OrderStatus {
    created(false),
    trading(false),
    withdrawn(true),
    overflow(true),
    fulfilled(true);

    private final boolean isClosed;

    OrderStatus(boolean isClosed) {this.isClosed = isClosed;}

    public boolean isClosed() {
        return isClosed;
    }
}
