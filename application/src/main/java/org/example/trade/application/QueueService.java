package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.asset.*;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.TradeSide;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation= Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
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
    public void handle(AssetUpdated assetEvent) {
        Asset asset = assetRepository.findById(assetEvent.account());
        OrderQueue orderQueue = orderQueueRepository.getInstance(assetEvent.account());
        if (assetEvent instanceof AssetCashUpdated) {
            tryAllocateToPeek(asset, orderQueue, TradeSide.BUY);
        } else if (assetEvent instanceof AssetPositionUpdated) {
            tryAllocateToPeek(asset, orderQueue, TradeSide.SELL);
        }
    }

    private void tryAllocateToPeek(Asset asset, OrderQueue orderQueue, TradeSide tradeSide) {
        if (orderQueue.isEmpty(tradeSide)) { return; }

        Order o = orderQueue.peek(tradeSide);
        log.info("准备分配: {} 或 {}", o.requirement().value(), o.requirement().shares() );
        log.info("拥有: {} 或 {}", asset.usableCash(), asset.usablePositions().get(o.requirement().securityCode()));

        Resource<?> r = asset.tryAllocate(o);
        if (r != null) {
            log.info("订单: {} 资源分配完成: {}", o.id(), r);
            orderQueue.dequeue(o);
            DomainEventBus.instance().publish(asset);
            assetRepository.save(asset);
        }
    }

}
