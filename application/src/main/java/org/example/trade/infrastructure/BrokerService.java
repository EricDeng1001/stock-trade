package org.example.trade.infrastructure;

import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;

public interface BrokerService {

    void submit(Order order);

    void withdraw(OrderId order);

}
