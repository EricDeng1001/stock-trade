package org.example.trade.interfaces;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.request.TradeRequest;

import java.util.List;

public interface OrderService {

    OrderId createOrder(TradeRequest tradeRequest, AccountId account);

    Order queryOrder(OrderId id);

    List<Order> queryOrder(AccountId accountId);

    Iterable<Order> getAll();

}
