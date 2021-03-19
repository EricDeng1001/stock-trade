package org.example.trade.domain.asset;

import org.example.trade.domain.account.AccountId;

public interface AssetRepository {

    Asset findById(AccountId id);

    void save(Asset asset);

}
