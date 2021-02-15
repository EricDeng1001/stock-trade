package org.example.trade.domain.tradeorder;

import java.time.Instant;

public final class OrderFinished extends TradeEvent {

    private final OrderStatus orderStatus;

    protected OrderFinished(Instant occurredOn, TradeOrder.Id id,
                            OrderStatus orderStatus) {
        super(occurredOn, id);
        this.orderStatus = orderStatus;
    }

    public OrderFinished(TradeOrder.Id id, OrderStatus orderStatus) {
        this(Instant.now(), id, orderStatus);
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    @Override
    public String toString() {
        return "OrderFinished{" +
            "orderStatus=" + orderStatus +
            '}';
    }

}
