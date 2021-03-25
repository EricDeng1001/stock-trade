package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.account.asset.AssetRepository;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.*;
import org.example.trade.infrastructure.SingleAccountBrokerService;
import org.example.trade.infrastructure.SingleAccountBrokerServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional(isolation= Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class AssetService extends DomainEventSubscriber<OrderUpdated> {

    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;

    private final OrderRepository orderRepository;

    private final SingleAccountBrokerServiceFactory factory;

    @Autowired
    public AssetService(AssetRepository assetRepository, OrderRepository orderRepository,
                        SingleAccountBrokerServiceFactory factory) {
        this.assetRepository = assetRepository;
        this.orderRepository = orderRepository;
        this.factory = factory;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    public void handle(OrderUpdated orderEvent) {
        Order order = orderRepository.findById(orderEvent.orderId());
        Asset asset = assetRepository.findById(order.account());
        if (asset == null) { throw new NoSuchElementException(); }
        if (orderEvent instanceof OrderTraded) {
            if (!asset.consume(order.id(), ((OrderTraded) orderEvent).deal())) {
                log.warn("券商交易数量超过分配数量，现有资源: {}", asset.resourceOf(orderEvent.orderId()));
            }
        } else if (orderEvent instanceof OrderFinished) {
            asset.reclaim(order.id());
            log.info("回收订单{} 的资源", order.id());
        }
        assetRepository.save(asset);
        DomainEventBus.instance().publish(asset);
    }

    public Asset queryAsset(AccountId accountId) {
        return assetRepository.findById(accountId);
    }

    public void syncAssetFromBroker(AccountId accountId) {
        log.info("开始同步账户资产: {}", accountId);
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
        log.info("完成同步账户资产: {}, {}", accountId, asset);
        DomainEventBus.instance().publish(asset);
    }

}
