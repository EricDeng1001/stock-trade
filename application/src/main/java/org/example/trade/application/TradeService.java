package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.asset.ResourceAllocated;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.infrastructure.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeService extends DomainEventSubscriber<ResourceAllocated> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    private final OrderRepository orderRepository;

    private final BrokerService brokerService;

    public TradeService(OrderRepository orderRepository,
                        BrokerService brokerService) {
        this.orderRepository = orderRepository;
        this.brokerService = brokerService;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    public synchronized void handle(ResourceAllocated resourceAllocated) {
        OrderId id = resourceAllocated.order();
        Order order = orderRepository.findById(id);
        brokerService.submit(order);
    }

    public synchronized void orderSubmitted(OrderId orderId, String brokerId) {
        Order order = orderRepository.findById(orderId);
        order.trading(brokerId);
        orderRepository.save(order);
    }

}
