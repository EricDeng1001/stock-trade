package org.example.trade.domain.order;

import java.time.Instant;

public final class OrderFinished extends OrderUpdated {

    private final OrderStatus orderStatus;

    protected OrderFinished(Instant occurredOn, OrderId id,
                            OrderStatus orderStatus) {
        super(occurredOn, id);
        this.orderStatus = orderStatus;
    }

    public OrderFinished(OrderId id, OrderStatus orderStatus) {
        this(Instant.now(), id, orderStatus);
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    @Override
    public String toString() {
        return "OrderFinished{" +
            "orderId=" + orderId +
            ", orderStatus=" + orderStatus +
            ", occurredOn=" + occurredOn +
            "} ";
    }

}
