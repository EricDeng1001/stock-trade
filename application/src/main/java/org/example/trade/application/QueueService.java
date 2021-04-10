package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.*;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.domain.order.OrderStatus;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class QueueService implements org.example.trade.interfaces.QueueService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    private final OrderRepository orderRepository;

    private final SingleAccountBrokerServiceFactory factory;

    @Autowired
    public QueueService(AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository,
                        OrderRepository orderRepository,
                        SingleAccountBrokerServiceFactory factory) {
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        this.orderRepository = orderRepository;
        this.factory = factory;
        DomainEventBus.instance().subscribe(AssetCashUpdated.class, this::tryAllocateToBuy);
        DomainEventBus.instance().subscribe(AssetPositionUpdated.class, this::tryAllocateToSell);
        DomainEventBus.instance().subscribe(ResourceAllocated.class, this::submit);
    }

    @Override
    public boolean enqueue(OrderId id) {
        Order order = orderRepository.findById(id);
        if (order == null) { throw new NoSuchElementException("订单不存在"); }
        return enqueue(order);
    }

    @Override
    public Map<OrderId, Boolean> enqueueAll(AccountId accountId) {
        List<Order> orders = orderRepository.findNewByAccount(accountId);
        Map<OrderId, Boolean> r = new HashMap<>(orders.size());
        for (Order o : orders) {
            r.put(o.id(), enqueue(o));
        }
        return r;
    }

    @Override
    public boolean dequeue(OrderId id) {
        Order order = orderRepository.findById(id);
        if (order.isTrading()) {
            factory.getOrNew(id.accountId()).withdraw(id);
            return true;
        } else {
            OrderQueue queue = orderQueueRepository.getInstance(order.account());
            return queue.dequeue(order);
        }
    }

    private void submit(ResourceAllocated resourceAllocated) {
        OrderId id = resourceAllocated.order();
        Order order = orderRepository.findById(id);
        factory.getOrNew(id.accountId()).submit(order);
        logger.info("已向券商提交订单: {}", id);
    }

    @Transactional
    private void tryAllocateToBuy(AssetCashUpdated assetEvent) {
        Asset asset = assetRepository.findById(assetEvent.account());
        OrderQueue orderQueue = orderQueueRepository.getInstance(assetEvent.account());
        if (orderQueue.isEmpty()) { return; }
        Order o = orderQueue.peek();
        tryAlloc(asset, orderQueue, o);
    }

    @Transactional
    private void tryAllocateToSell(AssetPositionUpdated assetEvent) {
        Asset asset = assetRepository.findById(assetEvent.account());
        OrderQueue orderQueue = orderQueueRepository.getInstance(assetEvent.account());
        if (orderQueue.isEmpty(assetEvent.securityCode())) { return; }
        Order o = orderQueue.peek(assetEvent.securityCode());
        tryAlloc(asset, orderQueue, o);
    }

    private void tryAlloc(Asset asset, OrderQueue orderQueue, Order o) {
        Resource<?> r = asset.tryAllocate(o);
        if (r != null) {
            orderQueue.dequeue(o);
            assetRepository.save(asset);
            logger.info("订单: {} 资源分配完成: {}", o.id(), r);
            DomainEventBus.instance().publish(asset);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean enqueue(Order order) {
        if (order.status() != OrderStatus.created) {
            logger.warn("用户在尝试将非新建订单加入队列");
            return false;
        }
        Asset asset = assetRepository.findById(order.account());
        Resource<?> r = asset.tryAllocate(order);
        if (r == null) {
            logger.info("将订单加入到队列: {}", order.id());
            OrderQueue orderQueue = orderQueueRepository.getInstance(order.account());
            orderQueue.enqueue(order);
        } else {
            logger.info("订单: {} 资源分配完成: {}", order.id(), r);
            assetRepository.save(asset);
            DomainEventBus.instance().publish(asset);
        }
        return true;
    }

}
