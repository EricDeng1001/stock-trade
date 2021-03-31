package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.account.asset.AssetRepository;
import org.example.trade.domain.order.OrderClosed;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderTraded;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.example.trade.infrastructure.broker.SingleAccountBrokerServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;

    private final SingleAccountBrokerServiceFactory factory;

    @Autowired
    public AssetService(AssetRepository assetRepository,
                        SingleAccountBrokerServiceFactory factory) {
        this.assetRepository = assetRepository;
        this.factory = factory;
        DomainEventBus.instance().subscribe(OrderTraded.class, this::handleOrderTraded);
        DomainEventBus.instance().subscribe(OrderClosed.class, this::handleOrderClosed);
    }

    public void syncAssetFromBroker(AccountId accountId) {
        SingleAccountBrokerService service = factory.getOrNew(accountId);
        service.queryAsset();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Asset queryAsset(AccountId accountId) {
        return assetRepository.findById(accountId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean handleOrderClosed(OrderClosed orderEvent) {
        OrderId orderId = orderEvent.orderId();
        logger.info("尝试开始处理close: {}", orderId.uid());
        logger.info("开始处理close: {}", orderId.uid());
        logger.info("尝试获取asset锁: {}", orderId.uid());
        Asset asset = assetRepository.findById(orderId.accountId());
        logger.info("获取asset锁 {}", orderId.uid());
        asset.reclaim(orderId);
        assetRepository.save(asset);
        logger.info("回收订单{} 的资源", orderId.uid());
        DomainEventBus.instance().publish(asset);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean handleOrderTraded(OrderTraded orderEvent) {
        OrderId orderId = orderEvent.orderId();
        logger.info("尝试开始处理trade: {}", orderId.uid());
        logger.info("开始处理trade: {}", orderId.uid());
        logger.info("尝试获取asset锁: {}", orderId.uid());
        Asset asset = assetRepository.findById(orderId.accountId());
        logger.info("获取asset锁 {}", orderId.uid());
        boolean overDealt = !asset.consume(orderId, orderEvent.deal());
        assetRepository.save(asset);
        logger.info("asset updated for {}", orderId.uid());
        if (overDealt) {
            logger.warn("券商交易数量超过分配数量，{} 现有资源: {}", orderId.uid(), asset.resourceOf(orderId));
        }
        DomainEventBus.instance().publish(asset);
        return true;
    }

}
