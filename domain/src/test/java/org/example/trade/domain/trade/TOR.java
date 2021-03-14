package org.example.trade.domain.trade;

import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TOR implements OrderRepository {

    private final Map<Order.Id, Order> orderMap = new ConcurrentHashMap<>();

    @Override
    public Order findById(Order.Id id) {
        return orderMap.get(id);
    }

    @Override
    public void save(Order order) {
        orderMap.put(order.id(), order);
    }

}
