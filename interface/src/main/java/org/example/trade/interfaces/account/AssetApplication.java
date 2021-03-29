package org.example.trade.interfaces.account;

public interface AssetApplication {

    AssetDTO queryAsset(String accountId);

    void syncAsset(String accountId);

}
