package org.example.trade.domain.order.request;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.TradeSide;

public final class MarketTradeRequest extends TradeRequest {

    private final Price low;

    private final Price high;

    public MarketTradeRequest(SecurityCode securityCode,
                              Shares shares, TradeSide tradeSide, Price low, Price high) {
        super(securityCode, shares, tradeSide, PriceType.MARKET);
        this.low = low;
        this.high = high;
    }

    @Override
    public Money value() {
        return switch (tradeSide) {
            case BUY -> high.multiply(shares);
            case SELL -> low.multiply(shares);
        };
    }

    @Override
    public Price price() {
        return null;
    }

}
