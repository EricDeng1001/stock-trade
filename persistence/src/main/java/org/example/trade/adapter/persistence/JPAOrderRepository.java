package org.example.trade.adapter.persistence;

import org.example.trade.adapter.persistence.model.account.AccountId;
import org.example.trade.adapter.persistence.model.order.Order;
import org.example.trade.adapter.persistence.model.order.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPAOrderRepository extends JpaRepository<Order, OrderId> {

    List<Order> findAllByIdAccountId(AccountId id);

}
