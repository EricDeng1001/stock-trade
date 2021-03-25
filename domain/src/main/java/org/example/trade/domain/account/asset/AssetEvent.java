package org.example.trade.domain.account.asset;

import engineering.ericdeng.architecture.domain.model.DomainEvent;
import org.example.trade.domain.account.AccountId;

import java.time.Instant;

public abstract class AssetEvent extends DomainEvent {

    protected final AccountId account;

    protected AssetEvent(Instant occurredOn, AccountId account) {
        super(occurredOn);
        this.account = account;
    }

    public AccountId account() {
        return account;
    }

}
