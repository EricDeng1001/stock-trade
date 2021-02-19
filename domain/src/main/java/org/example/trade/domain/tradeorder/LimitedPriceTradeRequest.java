package org.example.trade.domain.tradeorder;

import org.example.trade.domain.market.Money;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.SecurityCode;

public final class LimitedPriceTradeRequest extends TradeRequest {

    private final Price targetPrice;

    public LimitedPriceTradeRequest(SecurityCode securityCode,
                                    RegularizedShares shares, TradeSide tradeSide, Price targetPrice) {
        super(securityCode, shares, tradeSide, PriceType.LIMITED);
        this.targetPrice = targetPrice;
    }

    public Price targetPrice() {
        return targetPrice;
    }

    @Override
    public String toString() {
        return "LimitedPriceTradeRequest{" +
            "targetPrice=" + targetPrice +
            '}';
    }

    /**
     * @return net value of this request, regardless of tradeSide
     */
    public Money netValue() {
        return targetPrice.multiply(shares);
    }

}
