package org.example.trade.domain.account.asset;

import org.example.trade.domain.account.AccountId;

public interface AssetRepository {

    Asset findById(AccountId id);

    void save(Asset asset);

    boolean exists(AccountId id);
}
