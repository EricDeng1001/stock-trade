package org.example.trade.interfaces;

import org.example.trade.interfaces.input.Order;

public interface OrderService {

    void placeOrderRetryUntilSuccessPlaced(Order order);

    void placeOrderTryOnce(Order order);

}
