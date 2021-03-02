package org.example.trade.domain.tradeorder;

import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;

public final class MarketTradeRequest extends TradeRequest {

    public MarketTradeRequest(SecurityCode securityCode,
                              Shares shares, TradeSide tradeSide) {
        super(securityCode, shares, tradeSide, PriceType.MARKET);
    }

}
