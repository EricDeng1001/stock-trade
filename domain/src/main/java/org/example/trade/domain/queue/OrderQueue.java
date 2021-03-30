package org.example.trade.domain.queue;

import engineering.ericdeng.architecture.domain.model.annotation.AggregateRoot;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.order.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 可并发使用
 */
@AggregateRoot
public class OrderQueue {

    private final AccountId account;

    private final Map<SecurityCode, ConcurrentLinkedDeque<Order>> sells;

    private final ConcurrentLinkedDeque<Order> buys;

    public OrderQueue(AccountId account,
                      ConcurrentHashMap<SecurityCode, ConcurrentLinkedDeque<Order>> sells,
                      ConcurrentLinkedDeque<Order> buys) {
        this.account = account;
        this.sells = sells;
        this.buys = buys;
    }

    public OrderQueue(AccountId account) {
        this(account, new ConcurrentHashMap<>(), new ConcurrentLinkedDeque<>());
    }

    public void enqueue(Order order) {
        switch (order.requirement().tradeSide()) {
            case BUY:
                buys.add(order);
                break;
            case SELL: {
                SecurityCode key = order.requirement().securityCode();
                ConcurrentLinkedDeque<Order> deque = sells.get(key);
                if (deque == null) {
                    deque = new ConcurrentLinkedDeque<>();
                    sells.put(key, deque);
                }
                deque.add(order);
            }
        }
    }

    public boolean dequeue(Order order) {
        switch (order.requirement().tradeSide()) {
            case BUY:
                return buys.remove(order);
            case SELL: {
                SecurityCode key = order.requirement().securityCode();
                ConcurrentLinkedDeque<Order> deque = sells.get(key);
                if (deque == null) {
                    return false;
                }
                return deque.remove(order);
            }
        }
        return false;
    }

    public AccountId account() {
        return account;
    }

    public Order peek() {
        return buys.peek();
    }

    public Order peek(SecurityCode securityCode) {
        ConcurrentLinkedDeque<Order> deque = sells.get(securityCode);
        if (deque == null) {
            return null;
        }
        return deque.peek();
    }

    public boolean isEmpty(SecurityCode securityCode) {
        ConcurrentLinkedDeque<Order> deque = sells.get(securityCode);
        if (deque == null) {
            return true;
        }
        return deque.isEmpty();
    }

    public boolean isEmpty() {
        return buys.isEmpty();
    }

}
