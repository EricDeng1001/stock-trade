package org.example.trade.domain.account.asset;

import engineering.ericdeng.architecture.domain.model.annotation.Entity;
import org.example.finance.domain.Money;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.TradeSide;

@Entity
public final class CashResource extends Resource<Money> {

    private Money cash;

    public CashResource(SecurityCode securityCode, Money amount) {
        super(securityCode);
        this.cash = amount;
    }

    @Override
    boolean consume(Deal deal) {
        Money amount = deal.value();
        cash = cash.subtract(amount);
        return cash.compareTo(Money.ZERO) >= 0;
    }

    @Override
    public Money remain() {
        return cash;
    }

    @Override
    TradeSide usedFor() {
        return TradeSide.BUY;
    }

    @Override
    public String toString() {
        return "CashResource{" +
            "cash=" + cash +
            '}';
    }

}