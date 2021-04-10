package org.example.trade.interfaces;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.Asset;
import org.springframework.transaction.annotation.Transactional;

public interface AssetService {

    void syncAssetFromBroker(AccountId accountId);

    @Transactional
    Asset queryAsset(AccountId accountId);

}
