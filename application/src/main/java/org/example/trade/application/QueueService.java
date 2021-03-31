package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.asset.*;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueueService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    @Autowired
    public QueueService(AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository) {
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        DomainEventBus.instance().subscribe(AssetCashUpdated.class, this::tryAllocateToBuy);
        DomainEventBus.instance().subscribe(AssetPositionUpdated.class, this::tryAllocateToSell);
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

}
