package org.example.trade.application;

import org.example.trade.adapter.broker.SingleAccountBrokerServiceFactory;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetRepository;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements org.example.trade.interfaces.OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    private final SingleAccountBrokerServiceFactory factory;

    @Autowired
    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository,
                        SingleAccountBrokerServiceFactory factory) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        this.factory = factory;
    }

    @Override
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

    @Override
    public Order queryOrder(OrderId id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> queryOrder(AccountId accountId) {
        return orderRepository.findAllByAccount(accountId);
    }



    @Override
    public Iterable<Order> getAll() {
        return orderRepository.findAll();
    }

}
