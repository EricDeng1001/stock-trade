package org.example.trade.domain.market;

public class FiveLevelPrice extends LevelPrice {

    public FiveLevelPrice(Price[] bids, Price[] asks) {
        super(5, bids, asks);
    }

}
