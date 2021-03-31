package org.example.trade.interfaces.order;

import java.util.List;
import java.util.Map;

public interface OrderApplication {

    String createOrder(CreateOrderCommand command);

    OrderDTO queryOrder(String orderId);

    List<OrderDTO> queryOrdersOfAccount(String accountId);

    List<TradeDTO> queryTradesOfAccount(String accountId);

    boolean enqueueOrder(String orderIdDTO);

    Map<String, Boolean> enqueueAll(String accountId);

    boolean dequeueOrder(String orderIdDTO);

    Iterable<OrderDTO> getAll();

}
