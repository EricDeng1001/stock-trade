package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation= Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class DealService {

    private static final Logger log = LoggerFactory.getLogger(DealService.class);

    private final OrderRepository orderRepository;

    @Autowired
    public DealService(OrderRepository orderRepository) {this.orderRepository = orderRepository;}

    public void orderSubmitted(OrderId orderId, String brokerId) {
        Order order = orderRepository.findById(orderId);
        order.submitted(brokerId);
        orderRepository.save(order);
        log.info("订单开始交易: {}", orderId);
    }

    public void newDeal(OrderId id, Deal deal, String brokerId) {
        Order order = orderRepository.findById(id);
        order.makeDeal(deal, brokerId);
        orderRepository.save(order);
        log.info("订单取得交易: {}, 成交: {}", id, deal);
        DomainEventBus.instance().publish(order);
    }

    public void finish(OrderId id) {
        Order order = orderRepository.findById(id);
        order.close();
        orderRepository.save(order);
        log.info("订单关闭交易: {}", id);
        DomainEventBus.instance().publish(order);
    }

}
