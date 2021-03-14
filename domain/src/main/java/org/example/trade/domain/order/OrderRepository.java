package org.example.trade.domain.order;

public interface OrderRepository {

    Order findById(Order.Id id);

    void save(Order order);

}
