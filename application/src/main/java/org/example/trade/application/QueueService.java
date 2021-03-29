package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.asset.*;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueueService extends DomainEventSubscriber<AssetUpdated> {

    private static final Logger log = LoggerFactory.getLogger(QueueService.class);

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    @Autowired
    public QueueService(AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository) {
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException.class)
    public void handle(AssetUpdated assetEvent) {
        log.info("try get lock queue");
        Asset asset = assetRepository.findById(assetEvent.account());
        log.info("get lock queue");
        OrderQueue orderQueue = orderQueueRepository.getInstance(assetEvent.account());
        if (assetEvent instanceof AssetCashUpdated) {
            tryAllocateToBuy(asset, orderQueue);
        } else if (assetEvent instanceof AssetPositionUpdated) {
            tryAllocateToSell(asset, orderQueue, ((AssetPositionUpdated) assetEvent).securityCode());
        }
    }

    private void tryAllocateToBuy(Asset asset, OrderQueue orderQueue) {
        if (orderQueue.isEmpty()) { return; }
        Order o = orderQueue.peek();
        log.info("准备分配: {}, {}", o.requirement().securityCode(), o.requirement().value());
        log.info("拥有: {}", asset.usableCash());
        tryAlloc(asset, orderQueue, o);
    }

    private void tryAllocateToSell(Asset asset, OrderQueue orderQueue, SecurityCode securityCode) {
        if (orderQueue.isEmpty(securityCode)) { return; }
        Order o = orderQueue.peek(securityCode);
        log.info("准备分配: {}, {}", o.requirement().securityCode(), o.requirement().shares());
        log.info("拥有: {}", asset.usablePositions().get(o.requirement().securityCode()));
        tryAlloc(asset, orderQueue, o);
    }

    private void tryAlloc(Asset asset, OrderQueue orderQueue, Order o) {
        Resource<?> r = asset.tryAllocate(o);
        if (r != null) {
            orderQueue.dequeue(o);
            assetRepository.save(asset);
            log.info("订单: {} 资源分配完成: {}", o.id(), r);
            DomainEventBus.instance().publish(asset);
        }
    }

}
