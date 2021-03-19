package org.example.trade.infrastructure;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.asset.Asset;
import org.example.trade.domain.asset.AssetRepository;

public class JavaMapAssetRepository extends JavaMapRepository<AccountId, Asset> implements AssetRepository {

    @Override
    protected AccountId getId(Asset r) {
        return r.id();
    }

}
