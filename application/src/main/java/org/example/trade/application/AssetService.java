package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.account.asset.AssetRepository;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.OrderClosed;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderTraded;
import org.example.trade.domain.order.OrderUpdated;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.example.trade.infrastructure.broker.SingleAccountBrokerServiceFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class AssetService extends DomainEventSubscriber<OrderUpdated> {

    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;

    private final SingleAccountBrokerServiceFactory factory;

    private final ConcurrentHashMap<OrderId, ReadWriteLock> handlingLocks = new ConcurrentHashMap<>();

    @Autowired
    public AssetService(AssetRepository assetRepository,
                        SingleAccountBrokerServiceFactory factory) {
        this.assetRepository = assetRepository;
        this.factory = factory;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(OrderUpdated orderEvent) {
        if (orderEvent instanceof OrderTraded) {
            handleOrderTraded((OrderTraded) orderEvent);
        } else if (orderEvent instanceof OrderClosed) {
            handleOrderFinished((OrderClosed) orderEvent);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Asset queryAsset(AccountId accountId) {
        return assetRepository.findById(accountId);
    }

    @Transactional
    public void syncAssetFromBroker(AccountId accountId) {
        SingleAccountBrokerService service = factory.getOrNew(accountId);
        AssetInfo brokerAssetInfo = service.queryAsset();
        Asset asset = assetRepository.findById(accountId);
        if (asset == null) {
            asset = new Asset(accountId, brokerAssetInfo);
        } else {
            asset.set(brokerAssetInfo.usableCash());
            for (Map.Entry<SecurityCode, Shares> e : brokerAssetInfo.usablePositions().entrySet()) {
                asset.set(e.getKey(), e.getValue());
            }
        }
        assetRepository.save(asset);
        logger.info("完成同步账户资产: {}", accountId);
        DomainEventBus.instance().publish(asset);
    }

    private void handleOrderFinished(OrderClosed orderEvent) {
        OrderId orderId = orderEvent.orderId();
        Lock writeLock = handlingLocks.computeIfAbsent(orderId, o -> new ReentrantReadWriteLock()).writeLock();
        try {
            writeLock.lock();
            Asset asset = getAsset(orderId);
            asset.reclaim(orderId);
            assetRepository.save(asset);
            handlingLocks.remove(orderId);
            logger.info("回收订单{} 的资源", orderId);
            DomainEventBus.instance().publish(asset);
        } finally {
            writeLock.unlock();
        }
    }

    private void handleOrderTraded(OrderTraded orderEvent) {
        OrderId orderId = orderEvent.orderId();
        Lock readLock = handlingLocks.computeIfAbsent(orderId, o -> new ReentrantReadWriteLock()).readLock();
        try {
            readLock.lock();
            Asset asset = getAsset(orderId);
            boolean overDealt = !asset.consume(orderId, orderEvent.deal());
            assetRepository.save(asset);
            if (overDealt) {
                logger.warn("券商交易数量超过分配数量，{} 现有资源: {}", orderId, asset.resourceOf(orderId));
            }
            logger.info("asset updated for {}", orderId.uid());
            DomainEventBus.instance().publish(asset);
        } finally {
            readLock.unlock();
        }
    }

    @NotNull
    private Asset getAsset(OrderId orderId) {
        logger.info("try to get lock for {}", orderId.uid());
        Asset asset = assetRepository.findById(orderId.accountId());
        logger.info("get lock for {}", orderId.uid());
        if (asset == null) { throw new NoSuchElementException(); }
        return asset;
    }

}
