package org.example.trade.domain.asset;

import org.example.finance.domain.Money;
import org.example.trade.domain.account.AccountId;

import java.time.Instant;

public class AssetGainCashed extends AssetUpdated {

    private final Money amount;

    private final Money nowAvailable;

    public AssetGainCashed(Instant occurredOn, AccountId id, Money amount, Money nowAvailable) {
        super(occurredOn, id);
        this.amount = amount;
        this.nowAvailable = nowAvailable;
    }

    public Money amount() {
        return amount;
    }

    public Money nowAvailable() {
        return nowAvailable;
    }

    @Override
    public String toString() {
        return "AssetGainCashed{" +
            "account=" + account +
            ", amount=" + amount +
            ", nowAvailable=" + nowAvailable +
            ", occurredOn=" + occurredOn +
            '}';
    }

}
