package org.example.trade.domain.tradeorder;

import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;

public class TradeRequest {

    protected final SecurityCode securityCode;

    protected final Shares shares;

    protected final TradeSide tradeSide;

    protected final PriceType priceType;

    protected TradeRequest(SecurityCode securityCode, Shares shares, TradeSide tradeSide,
                           PriceType priceType) {
        this.securityCode = securityCode;
        this.shares = shares;
        this.tradeSide = tradeSide;
        this.priceType = priceType;
    }

    public SecurityCode securityCode() {
        return securityCode;
    }

    public Shares shares() {
        return shares;
    }

    public TradeSide tradeSide() {
        return tradeSide;
    }

    public PriceType priceType() {
        return priceType;
    }

    @Override
    public String toString() {
        return "TradeRequest{" +
            "stockCode=" + securityCode +
            ", shares=" + shares +
            ", tradeSide=" + tradeSide +
            ", priceType=" + priceType +
            '}';
    }

}
