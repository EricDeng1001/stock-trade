package org.example.trade.adapter.jpa;

import org.example.trade.adapter.jpa.model.order.Order;
import org.example.trade.adapter.jpa.translator.AccountTranslator;
import org.example.trade.adapter.jpa.translator.OrderTranslator;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.domain.order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryJpaAdapter implements OrderRepository {

    private final JpaOrderRepository orderRepository;

    public OrderRepositoryJpaAdapter(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public org.example.trade.domain.order.Order findById(OrderId id) {
        Optional<Order> record = orderRepository.findById(
            OrderTranslator.from(id)
        );
        if (record.isEmpty()) { return null; }
        return OrderTranslator.from(record.get());
    }

    @Override
    public void save(org.example.trade.domain.order.Order order) {
        try {
            orderRepository.save(OrderTranslator.from(order));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int nextId() {
        return (int) orderRepository.count();
    }

    @Override
    public List<org.example.trade.domain.order.Order> findAll() {
        return orderRepository.findAll().stream().map(OrderTranslator::from).collect(Collectors.toList());
    }

    @Override
    public List<org.example.trade.domain.order.Order> findNewByAccount(AccountId accountId) {
        return orderRepository.findAllByIdAccountIdAndStatus(AccountTranslator.from(accountId), OrderStatus.created)
            .stream().map(OrderTranslator::from)
            .collect(Collectors.toList());
    }

    @Override
    public List<org.example.trade.domain.order.Order> findAllByAccount(AccountId accountId) {
        return orderRepository.findAllByIdAccountId(AccountTranslator.from(accountId))
            .stream().map(OrderTranslator::from)
            .collect(Collectors.toList());
    }

}
