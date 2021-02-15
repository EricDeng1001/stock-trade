package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;

public class MarketTradeRequest extends TradeRequest {

    public MarketTradeRequest(StockCode stockCode,
                              RegularizedShares shares, TradeSide tradeSide, Account account) {
        super(stockCode, shares, tradeSide, PriceType.MARKET, account);
    }

}
