package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.account.asset.AssetRepository;
import org.example.trade.domain.account.asset.Resource;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.domain.order.OrderStatus;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
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
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    private final SingleAccountBrokerService brokerTradeService;

    @Autowired
    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository,
                        SingleAccountBrokerService brokerTradeService) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        this.brokerTradeService = brokerTradeService;
    }

    public OrderId createOrder(TradeRequest tradeRequest, AccountId account) {
        if (!assetRepository.exists(account)) { throw new IllegalArgumentException("所选账户不存在或没有资产信息"); }
        Order order = new Order(
            account,
            orderRepository.nextId(),
            tradeRequest
        );
        log.info("订单创建: {}", order.id());
        orderRepository.save(order);
        return order.id();
    }

    public Order queryOrder(OrderId id) {
        return orderRepository.findById(id);
    }

    public boolean enqueueOrder(OrderId id) {
        Order order = orderRepository.findById(id);
        if (order == null) { throw new NoSuchElementException("订单不存在"); }
        return enqueue(order);
    }

    public List<Order> queryOrder(AccountId accountId) {
        return orderRepository.findAllByAccount(accountId);
    }

    public boolean dequeueOrder(OrderId id) {
        Order order = orderRepository.findById(id);
        if (order.isTrading()) {
            brokerTradeService.withdraw(id);
            return true;
        } else {
            OrderQueue queue = orderQueueRepository.getInstance(order.account());
            return queue.dequeue(order);
        }
    }

    public Map<OrderId, Boolean> enqueueAll(AccountId accountId) {
        List<Order> orders = orderRepository.findNewByAccount(accountId);
        Map<OrderId, Boolean> r = new HashMap<>(orders.size());
        for (Order o : orders) {
            r.put(o.id(), enqueue(o));
        }
        return r;
    }

    public Iterable<Order> getAll() {
        return orderRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean enqueue(Order order) {
        if (order.status() != OrderStatus.created) {
            log.warn("用户在尝试将非新建订单加入队列");
            return false;
        }
        Asset asset = assetRepository.findById(order.account());
        Resource<?> r = asset.tryAllocate(order);
        if (r == null) {
            log.info("将订单加入到队列: {}", order.id());
            OrderQueue orderQueue = orderQueueRepository.getInstance(order.account());
            orderQueue.enqueue(order);
        } else {
            log.info("订单: {} 资源分配完成: {}", order.id(), r);
            assetRepository.save(asset);
            DomainEventBus.instance().publish(asset);
        }
        return true;
    }

}
