package org.example.trade.domain.account.asset;

import org.example.trade.domain.account.AccountId;

import java.time.Instant;

public abstract class AssetUpdated extends AssetEvent {

    private final AssetUpdateType type;

    protected AssetUpdated(Instant occurredOn, AccountId id) {
        super(occurredOn, id);
        this.type = AssetUpdateType.TradeDeal;
    }

    protected AssetUpdated(Instant occurredOn, AccountId id, AssetUpdateType type) {
        super(occurredOn, id);
        this.type = type;
    }
    public AssetUpdateType type() {
        return type;
    }

}
