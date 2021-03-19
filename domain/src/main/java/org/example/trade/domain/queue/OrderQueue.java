package org.example.trade.domain.queue;

import engineering.ericdeng.architecture.domain.model.annotation.AggregateRoot;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.TradeSide;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 可并发使用
 */
@AggregateRoot
public class OrderQueue {

    private final AccountId account;

    private final Queue<Order> sells;

    private final Queue<Order> buys;

    public OrderQueue(AccountId account,
                      ConcurrentLinkedDeque<Order> sells, ConcurrentLinkedDeque<Order> buys) {
        this.account = account;
        this.sells = sells;
        this.buys = buys;
    }

    public void enqueue(Order order) {
        switch (order.requirement().tradeSide()) {
            case BUY -> buys.add(order);
            case SELL -> sells.add(order);
        }
    }

    public boolean dequeue(Order order) {
        return switch (order.requirement().tradeSide()) {
            case BUY -> buys.remove(order);
            case SELL -> sells.remove(order);
        };
    }

    public AccountId account() {
        return account;
    }

    public boolean isEmpty(TradeSide tradeSide) {
        return switch (tradeSide) {
            case BUY -> buys.isEmpty();
            case SELL -> sells.isEmpty();
        };
    }

    public Order peek(TradeSide tradeSide) {
        return switch (tradeSide) {
            case BUY -> buys.peek();
            case SELL -> sells.peek();
        };
    }

}
