package org.example.trade.domain.asset;

import org.example.trade.domain.account.AccountId;

import java.time.Instant;

public abstract class AssetUpdated extends AssetEvent {

    protected AssetUpdated(Instant occurredOn, AccountId id) {
        super(occurredOn, id);
    }

}
