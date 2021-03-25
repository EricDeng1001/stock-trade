package org.example.trade.domain.order;

import engineering.ericdeng.architecture.domain.model.annotation.ValueObject;
import org.example.trade.domain.market.Shares;

import java.time.Instant;

@ValueObject
public final class Trade {

    private final String brokerId;

    private final Deal deal;

    private final Instant dealtOn;

    public Trade(String brokerId, Deal deal, Instant dealtOn) {
        this.brokerId = brokerId;
        this.deal = deal;
        this.dealtOn = dealtOn;
    }

    public Instant dealtOn() {
        return dealtOn;
    }

    @Override
    public String toString() {
        return "Trade{" +
            "id=" + brokerId +
            ", deal=" + deal +
            '}';
    }

    public String brokerId() {
        return brokerId;
    }

    public Deal deal() {
        return deal;
    }

    public Shares shares() {
        return deal.shares();
    }

}
