package org.example.trade.infrastructure.messaging;

import org.example.trade.domain.order.OrderClosed;
import org.example.trade.domain.order.OrderTraded;

public interface WebSocketNotificationService {

    void orderTraded(OrderTraded orderTraded);

    void orderClosed(OrderClosed orderClosed);
}
