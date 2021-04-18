package org.example.trade.interfaces;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;

public interface AssetService {

    void syncAssetFromBroker(AccountId accountId);

    Asset queryAsset(AccountId accountId);

}
