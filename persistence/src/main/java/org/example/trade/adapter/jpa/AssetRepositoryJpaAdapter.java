package org.example.trade.adapter.jpa;

import org.example.trade.adapter.jpa.model.account.asset.Asset;
import org.example.trade.adapter.jpa.translator.AccountTranslator;
import org.example.trade.adapter.jpa.translator.AssetTranslator;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AssetRepositoryJpaAdapter implements AssetRepository {

    private final JpaAssetRepository assetRepository;

    public AssetRepositoryJpaAdapter(JpaAssetRepository assetRepository) {
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
