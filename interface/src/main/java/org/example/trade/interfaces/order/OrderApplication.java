package org.example.trade.interfaces.order;

public interface OrderApplication {

    String createOrder(CreateOrderCommand command);

    OrderDTO queryOrder(String orderIdDTO);

    boolean enqueueOrder(String orderIdDTO);

    boolean enqueueAll(String accountId);

    boolean dequeueOrder(String orderIdDTO);

    Iterable<OrderDTO> getAll();

}
