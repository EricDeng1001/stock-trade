package org.example.trade.domain.market;

public class FiveLevelPrice {

    private final Price[] bids;

    private final Price[] asks;

    public FiveLevelPrice(Price[] bids, Price[] asks) {
        this.bids = bids;
        this.asks = asks;
    }

    public Price getBid(int index) {
        return bids[index];
    }

    public Price getAsk(int index) {
        return asks[index];
    }

}
