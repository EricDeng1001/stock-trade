package org.example.trade.domain.account.asset;

import engineering.ericdeng.architecture.domain.model.annotation.Entity;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.TradeSide;

@Entity
public final class PositionResource extends Resource<Shares> {

    private Shares shares;

    public PositionResource(SecurityCode securityCode, Shares amount) {
        super(securityCode);
        this.shares = amount;
    }

    @Override
    boolean consume(Deal deal) {
        Shares amount = deal.shares();
        shares = shares.subtract(amount);
        return shares.compareTo(Shares.ZERO) >= 0;
    }

    @Override
    public Shares remain() {
        return shares;
    }

    @Override
    TradeSide usedFor() {
        return TradeSide.SELL;
    }

    @Override
    public String toString() {
        return "PositionResource{" +
            "shares=" + shares +
            '}';
    }

}