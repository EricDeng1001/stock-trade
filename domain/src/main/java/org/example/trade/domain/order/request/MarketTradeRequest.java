package org.example.trade.domain.order.request;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.TradeSide;

public final class MarketTradeRequest extends TradeRequest {

    public MarketTradeRequest(SecurityCode securityCode,
                              Shares shares, TradeSide tradeSide) {
        super(securityCode, shares, tradeSide, PriceType.MARKET);
    }

    @Override
    public Money value() {
        return Money.ZERO;
    }

}
