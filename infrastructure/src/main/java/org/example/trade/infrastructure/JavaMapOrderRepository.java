package org.example.trade.infrastructure;

import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;

public class JavaMapOrderRepository extends JavaMapRepository<OrderId, Order> {

    @Override
    protected OrderId getId(Order r) {
        return r.id();
    }

}
