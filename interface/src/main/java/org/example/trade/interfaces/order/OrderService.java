package org.example.trade.interfaces.order;

public interface OrderService {

    OrderIdDTO createOrder(CreateOrderCommand command);

    OrderDTO queryOrder(OrderIdDTO orderIdDTO);

    void enqueueOrder(OrderIdDTO orderIdDTO);

    void dequeueOrder(OrderIdDTO orderIdDTO);

}
