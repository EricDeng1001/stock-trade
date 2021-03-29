package org.example.trade.domain.order;

import org.example.trade.domain.account.AccountId;

import java.util.List;

public interface OrderRepository {

    Order findById(OrderId id);

    void save(Order order);

    int nextId();

    List<Order> findAll();

    List<Order> findNewByAccount(AccountId accountId);

}
