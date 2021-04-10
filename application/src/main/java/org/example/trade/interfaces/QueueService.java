package org.example.trade.interfaces;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.OrderId;

import java.util.Map;

public interface QueueService {

    boolean enqueue(OrderId id);

    Map<OrderId, Boolean> enqueueAll(AccountId accountId);

    boolean dequeue(OrderId id);

}
