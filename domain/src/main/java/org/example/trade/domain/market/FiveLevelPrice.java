package org.example.trade.domain.market;

import org.example.finance.domain.Price;

public class FiveLevelPrice extends LevelPrice {

    public FiveLevelPrice(Price[] bids, Price[] asks) {
        super(5, bids, asks);
    }

}
