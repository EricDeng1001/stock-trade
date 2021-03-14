package org.example.trade.adapter;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderRepository;

import java.time.Instant;

public abstract class BrokerAdapter {

    private final OrderRepository orderRepository;

    private final AccountRepository accountRepository;

    protected BrokerAdapter(OrderRepository orderRepository,
                            AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
    }

    public abstract boolean startTradeOrder(Order order);

    public abstract boolean withdrawOrder(Order.Id id);

    /**
     * 这是唯一一个修改某个order状态的事务，所以在这个事务里面同时修改两个聚合没有问题
     */
    protected void onTrade(Order.Id id, Deal deal, Instant time) {
        Order order = orderRepository.findById(id);
        Account account = accountRepository.findById(order.account());
        account.exchange(order.id(), deal);
        order.makeDeal(deal, time);
        orderRepository.save(order);
        accountRepository.save(account);
    }

    protected void onWithdraw(Order.Id id, Instant time) {
        Order order = orderRepository.findById(id);
        Account account = accountRepository.findById(order.account());
        order.finish(time);
        account.finishOrder(id);
        orderRepository.save(order);
        accountRepository.save(account);
    }

}
