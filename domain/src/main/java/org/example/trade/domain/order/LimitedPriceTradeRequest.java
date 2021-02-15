package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;

public class LimitedPriceTradeRequest extends TradeRequest {

    private final Price targetPrice;

    public LimitedPriceTradeRequest(StockCode stockCode,
                                    RegularizedShares shares, TradeSide tradeSide, Price targetPrice, Account account) {
        super(stockCode, shares, tradeSide, PriceType.LIMITED, account);
        this.targetPrice = targetPrice;
    }

    public Price targetPrice() {
        return targetPrice;
    }

}
