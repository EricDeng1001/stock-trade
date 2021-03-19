package org.example.trade.domain.order.request;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.TradeSide;

public abstract class TradeRequest {

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

    public abstract Money value();

    public abstract Price price();

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
