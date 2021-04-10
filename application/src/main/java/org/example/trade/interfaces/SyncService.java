package org.example.trade.interfaces;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetInfo;
import org.springframework.transaction.annotation.Transactional;

public interface SyncService {

    @Transactional
    void syncAsset(AccountId accountId, AssetInfo brokerAssetInfo);

}
