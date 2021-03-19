package org.example.trade.application;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.asset.Asset;
import org.example.trade.domain.asset.AssetRepository;
import org.example.trade.domain.asset.Resource;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.domain.order.OrderStatus;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.example.trade.infrastructure.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    private final BrokerService brokerService;

    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository,
                        BrokerService brokerService) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        this.brokerService = brokerService;
    }

    public synchronized OrderId createOrder(TradeRequest tradeRequest, AccountId account) {
        Order order = new Order(
            account,
            orderRepository.nextId(),
            tradeRequest
        );
        orderRepository.save(order);
        return order.id();
    }

    public synchronized Order queryOrder(OrderId id) {
        return orderRepository.findById(id);
    }

    public synchronized boolean enqueueOrder(OrderId id) {
        Order order = orderRepository.findById(id);
        if (order.status() != OrderStatus.created) { return false; }
        Asset asset = assetRepository.findById(order.account());
        Resource<?> r = asset.tryAllocate(order);
        if (r == null) {
            OrderQueue orderQueue = orderQueueRepository.getInstance(order.account());
            orderQueue.enqueue(order);
        } else {
            assetRepository.save(asset);
        }
        return true;
    }

    public synchronized boolean dequeueOrder(OrderId id) {
        Order order = orderRepository.findById(id);
        if (order.isTrading()) {
            brokerService.withdraw(id);
            return true;
        } else {
            OrderQueue queue = orderQueueRepository.getInstance(order.account());
            return queue.dequeue(order);
        }
    }

}
