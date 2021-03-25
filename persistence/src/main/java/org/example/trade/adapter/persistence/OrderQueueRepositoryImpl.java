package org.example.trade.adapter.persistence;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderQueueRepositoryImpl implements OrderQueueRepository {

    Map<AccountId, OrderQueue> map = new ConcurrentHashMap<>();

    @Override
    public OrderQueue getInstance(AccountId id) {
        return map.get(id);
    }

    @Override
    public void add(OrderQueue orderQueue) {
        map.put(orderQueue.account(), orderQueue);
    }

}
