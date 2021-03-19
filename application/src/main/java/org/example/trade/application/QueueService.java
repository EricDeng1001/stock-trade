package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.asset.*;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.TradeSide;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueService extends DomainEventSubscriber<AssetUpdated> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    private final AssetRepository assetRepository;

    private final OrderQueueRepository orderQueueRepository;

    public QueueService(AssetRepository assetRepository,
                        OrderQueueRepository orderQueueRepository) {
        this.assetRepository = assetRepository;
        this.orderQueueRepository = orderQueueRepository;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    public synchronized void handle(AssetUpdated assetEvent) {
        Asset asset = assetRepository.findById(assetEvent.account());
        OrderQueue orderQueue = orderQueueRepository.getInstance(assetEvent.account());
        if (assetEvent instanceof AssetGainCashed) {
            tryAllocateToPeek(asset, orderQueue, TradeSide.BUY);
        } else if (assetEvent instanceof AssetGainPositioned) {
            tryAllocateToPeek(asset, orderQueue, TradeSide.SELL);
        }
    }

    private synchronized void tryAllocateToPeek(Asset asset, OrderQueue orderQueue, TradeSide tradeSide) {
        if (orderQueue.isEmpty(tradeSide)) { return; }
        Order o = orderQueue.peek(tradeSide);
        Resource<?> r = asset.tryAllocate(o);
        if (r != null) {
            orderQueue.dequeue(o);
        }
    }

}
