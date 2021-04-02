package org.example.trade.persistence;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetRepository;
import org.example.trade.persistence.model.account.asset.Asset;
import org.example.trade.persistence.translator.AccountTranslator;
import org.example.trade.persistence.translator.AssetTranslator;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AssetRepositoryImpl implements AssetRepository {

    private final JPAAssetRepository assetRepository;

    public AssetRepositoryImpl(JPAAssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public org.example.trade.domain.account.asset.Asset findById(AccountId id) {
        Optional<Asset> record = assetRepository.findById(AccountTranslator.from(id));
        if (record.isEmpty()) { return null; }
        return AssetTranslator.from(record.get());
    }

    @Override
    public void save(org.example.trade.domain.account.asset.Asset asset) {
        assetRepository.save(AssetTranslator.from(asset));
    }

    @Override
    public boolean exists(AccountId id) {
        return assetRepository.existsById(AccountTranslator.from(id));
    }

}
