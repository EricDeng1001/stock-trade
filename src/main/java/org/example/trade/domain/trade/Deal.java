package org.example.trade.domain.trade;

import org.example.trade.domain.market.Money;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.Shares;

public class Deal {

    private final Shares shares;

    private final Price price;

    public Deal(Shares shares, Price price) {
        this.shares = shares;
        this.price = price;
    }

    public Shares shares() {
        return shares;
    }

    public Price dealtPrice() {
        return price;
    }

    public Money tradeValue() {
        return price.multiply(shares);
    }

    @Override
    public String toString() {
        return "TradeUnit{" +
            "shares=" + shares +
            ", price=" + price +
            '}';
    }

}
