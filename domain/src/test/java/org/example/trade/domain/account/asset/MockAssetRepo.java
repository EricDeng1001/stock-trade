package org.example.trade.domain.account.asset;

import org.example.trade.domain.MockRepo;
import org.example.trade.domain.account.AccountId;

public class MockAssetRepo extends MockRepo<AccountId, Asset> implements AssetRepository {

    @Override
    protected AccountId getId(Asset r) {
        return r.id();
    }

}
