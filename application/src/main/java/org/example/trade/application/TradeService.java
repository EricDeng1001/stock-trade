package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.asset.ResourceAllocated;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.infrastructure.broker.OrderRejectedException;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeService extends DomainEventSubscriber<ResourceAllocated> {

    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    private final OrderRepository orderRepository;

    private final SingleAccountBrokerService brokerTradeService;

    @Autowired
    public TradeService(OrderRepository orderRepository,
                        SingleAccountBrokerService brokerTradeService) {
        this.orderRepository = orderRepository;
        this.brokerTradeService = brokerTradeService;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException.class)
    public void handle(ResourceAllocated resourceAllocated) {
        OrderId id = resourceAllocated.order();
        Order order = orderRepository.findById(id);
        try {
            String brokerId = brokerTradeService.submit(order);
            order.submitted(brokerId);
            orderRepository.save(order);
            logger.info("订单开始交易: {}", id);
        } catch (OrderRejectedException e) {
            order.close();
            orderRepository.save(order);
            DomainEventBus.instance().publish(order);
            logger.error("订单被拒绝: ", e);
        }
    }

}
