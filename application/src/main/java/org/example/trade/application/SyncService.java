package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.account.asset.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncService implements org.example.trade.interfaces.SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    private final AssetRepository assetRepository;

    public SyncService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional
    public void syncAsset(AccountId accountId, AssetInfo brokerAssetInfo) {
        Asset asset = assetRepository.findById(accountId);
        if (asset == null) {
            asset = new Asset(accountId, brokerAssetInfo);
        } else {
            asset.setTo(brokerAssetInfo);
        }
        assetRepository.save(asset);
        logger.info("完成同步账户资产: {}", accountId);
        DomainEventBus.instance().publish(asset);
    }

}
