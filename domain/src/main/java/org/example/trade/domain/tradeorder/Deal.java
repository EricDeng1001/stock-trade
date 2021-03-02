package org.example.trade.domain.tradeorder;

import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
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

    public Money value() {
        return price.multiply(shares);
    }

    @Override
    public String toString() {
        return "Deal{" +
            "shares=" + shares +
            ", price=" + price +
            '}';
    }

}
