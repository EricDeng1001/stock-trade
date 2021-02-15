package org.example.trade.domain.tradeorder;

import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;

public class TradeRequest {

    private final StockCode stockCode;

    private final RegularizedShares shares;

    private final TradeSide tradeSide;

    private final PriceType priceType;

    protected TradeRequest(StockCode stockCode, RegularizedShares shares, TradeSide tradeSide,
                           PriceType priceType) {
        this.stockCode = stockCode;
        this.shares = shares;
        this.tradeSide = tradeSide;
        this.priceType = priceType;
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

}
