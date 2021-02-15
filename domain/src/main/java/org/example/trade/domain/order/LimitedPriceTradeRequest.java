package org.example.trade.domain.order;

import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;

public final class LimitedPriceTradeRequest extends TradeRequest {

    private final Price targetPrice;

    public LimitedPriceTradeRequest(StockCode stockCode,
                                    RegularizedShares shares, TradeSide tradeSide, Price targetPrice) {
        super(stockCode, shares, tradeSide, PriceType.LIMITED);
        this.targetPrice = targetPrice;
    }

    public Price targetPrice() {
        return targetPrice;
    }

}
