package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.asset.Asset;
import org.example.trade.domain.asset.AssetRepository;
import org.example.trade.domain.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetService extends DomainEventSubscriber<OrderUpdated> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;

    private final OrderRepository orderRepository;

    public AssetService(AssetRepository assetRepository, OrderRepository orderRepository) {
        this.assetRepository = assetRepository;
        this.orderRepository = orderRepository;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    public synchronized void handle(OrderUpdated orderEvent) {
        Order order = orderRepository.findById(orderEvent.orderId());
        Asset asset = assetRepository.findById(order.account());
        if (orderEvent instanceof OrderTraded) {
            if (!asset.consume(order.id(), ((OrderTraded) orderEvent).deal())) {
                LOGGER.warn("券商多成交了");
            }
        } else if (orderEvent instanceof OrderFinished) {
            asset.reclaim(order.id());
        }
        assetRepository.save(asset);
    }

}
