package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.asset.ResourceAllocated;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.infrastructure.SingleAccountBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeService extends DomainEventSubscriber<ResourceAllocated> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

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
    public void handle(ResourceAllocated resourceAllocated) {
        OrderId id = resourceAllocated.order();
        Order order = orderRepository.findById(id);
        brokerTradeService.submit(order);
    }

}
