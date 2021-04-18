package org.example.trade.interfaces;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetInfo;

public interface SyncService {

    void syncAsset(AccountId accountId, AssetInfo brokerAssetInfo);

}
