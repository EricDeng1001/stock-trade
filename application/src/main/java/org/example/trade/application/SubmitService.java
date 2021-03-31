package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.asset.ResourceAllocated;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmitService {

    private static final Logger logger = LoggerFactory.getLogger(SubmitService.class);

    private final OrderRepository orderRepository;

    private final SingleAccountBrokerService brokerTradeService;

    @Autowired
    public SubmitService(OrderRepository orderRepository,
                         SingleAccountBrokerService brokerTradeService) {
        this.orderRepository = orderRepository;
        this.brokerTradeService = brokerTradeService;
        DomainEventBus.instance().subscribe(ResourceAllocated.class, this::submit);
    }

    public boolean submit(ResourceAllocated resourceAllocated) {
        OrderId id = resourceAllocated.order();
        Order order = orderRepository.findById(id);
        brokerTradeService.submit(order);
        logger.info("已向券商提交订单: {}", id);
        return true;
    }
}
