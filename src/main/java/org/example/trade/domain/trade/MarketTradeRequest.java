package org.example.trade.domain.trade;

import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;

public class MarketTradeRequest extends TradeRequest {

    public MarketTradeRequest(StockCode stockCode,
                              RegularizedShares shares, TradeSide tradeSide) {
        super(stockCode, shares, tradeSide, PriceType.MARKET);
    }

}
