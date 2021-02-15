package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;

public class TradeRequest {

    private final StockCode stockCode;

    private final RegularizedShares shares;

    private final TradeSide tradeSide;

    private final PriceType priceType;

    private final Account account;

    protected TradeRequest(StockCode stockCode, RegularizedShares shares, TradeSide tradeSide,
                           PriceType priceType, Account account) {
        this.stockCode = stockCode;
        this.shares = shares;
        this.tradeSide = tradeSide;
        this.priceType = priceType;
        this.account = account;
    }

    public StockCode stockCode() {
        return stockCode;
    }

    public RegularizedShares shares() {
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
            "stockCode=" + stockCode +
            ", shares=" + shares +
            ", tradeSide=" + tradeSide +
            ", priceType=" + priceType +
            '}';
    }

    public Account account() {
        return account;
    }

}
