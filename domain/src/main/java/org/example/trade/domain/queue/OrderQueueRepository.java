package org.example.trade.domain.queue;

import org.example.trade.domain.account.AccountId;

/**
 * 工作为集合模式
 */
public interface OrderQueueRepository {

    // 单例模式
    OrderQueue getInstance(AccountId id);

    void add(OrderQueue orderQueue);

}
