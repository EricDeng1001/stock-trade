package org.example.trade.domain.account.asset;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;

import java.time.Instant;

public class AssetPositionUpdated extends AssetUpdated {

    private final SecurityCode securityCode;

    private final Shares amount;

    private final Shares nowUsable;

    public AssetPositionUpdated(Instant occurredOn, AccountId id, SecurityCode securityCode,
                                Shares amount, Shares nowUsable) {
        this(occurredOn, id, securityCode, amount, nowUsable, AssetUpdateType.TradeDeal);
    }

    public AssetPositionUpdated(Instant occurredOn, AccountId id, SecurityCode securityCode,
                                Shares amount, Shares nowUsable, AssetUpdateType type) {
        super(occurredOn, id, type);
        this.securityCode = securityCode;
        this.amount = amount;
        this.nowUsable = nowUsable;
    }

    @Override
    public String toString() {
        return "AssetGainPositioned{" +
            "account=" + account +
            ", securityCode=" + securityCode +
            ", amount=" + amount +
            ", nowUsable=" + nowUsable +
            ", occurredOn=" + occurredOn +
            '}';
    }

    public SecurityCode securityCode() {
        return securityCode;
    }

    public Shares amount() {
        return amount;
    }

    public Shares nowUsable() {
        return nowUsable;
    }

}
