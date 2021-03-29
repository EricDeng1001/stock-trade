package org.example.trade.interfaces.order;

import java.util.List;

public interface OrderApplication {

    String createOrder(CreateOrderCommand command);

    OrderDTO queryOrder(String orderId);

    List<OrderDTO> queryOrdersOfAccount(String accountId);

    List<TradeDTO> queryTradesOfAccount(String accountId);

    boolean enqueueOrder(String orderIdDTO);

    boolean enqueueAll(String accountId);

    boolean dequeueOrder(String orderIdDTO);

    Iterable<OrderDTO> getAll();

}
