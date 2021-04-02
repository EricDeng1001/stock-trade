package org.example.trade.persistence;

import org.example.trade.domain.order.OrderStatus;
import org.example.trade.persistence.model.account.AccountId;
import org.example.trade.persistence.model.order.Order;
import org.example.trade.persistence.model.order.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPAOrderRepository extends JpaRepository<Order, OrderId> {

    List<Order> findAllByIdAccountIdAndStatus(AccountId id, OrderStatus status);

    List<Order> findAllByIdAccountId(AccountId accountId);
}
