package org.example.trade.domain.order.request;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.TradeSide;

public final class LimitedPriceTradeRequest extends TradeRequest {

    private final Price targetPrice;

    public LimitedPriceTradeRequest(SecurityCode securityCode,
                                    Shares shares, TradeSide tradeSide, Price targetPrice) {
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
    public Money value() {
        return targetPrice.multiply(shares);
    }

    @Override
    public Price price() {
        return targetPrice;
    }

}
