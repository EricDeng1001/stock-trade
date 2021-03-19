package org.example.trade.interfaces.order;

public interface OrderApplication {

    String createOrder(CreateOrderCommand command);

    OrderDTO queryOrder(String orderIdDTO);

    void enqueueOrder(String orderIdDTO);

    void dequeueOrder(String orderIdDTO);

}
