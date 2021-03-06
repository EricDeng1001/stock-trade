package org.example.trade.domain.queue;

import org.example.trade.domain.account.AccountId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockOrderQueueRepo implements OrderQueueRepository {

    Map<AccountId, OrderQueue> map = new ConcurrentHashMap<>();

    @Override
    public OrderQueue getInstance(AccountId id) {
        return map.get(id);
    }

    @Override
    public void add(OrderQueue orderQueue) {
        map.putIfAbsent(orderQueue.account(), orderQueue);
    }

}
