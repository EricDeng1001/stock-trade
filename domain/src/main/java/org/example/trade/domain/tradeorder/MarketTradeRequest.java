package org.example.trade.domain.tradeorder;

import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.SecurityCode;

public final class MarketTradeRequest extends TradeRequest {

    public MarketTradeRequest(SecurityCode securityCode,
                              RegularizedShares shares, TradeSide tradeSide) {
        super(securityCode, shares, tradeSide, PriceType.MARKET);
    }

}
