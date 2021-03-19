package org.example.trade.domain.order;

public interface OrderRepository {

    Order findById(OrderId id);

    void save(Order order);

    int nextId();
}
