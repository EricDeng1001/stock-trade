package org.example.trade.domain.market;

public abstract class LevelPrice {

    private final int levels;

    private final Price[] bids;

    private final Price[] asks;

    protected LevelPrice(int levels, Price[] bids, Price[] asks) {
        this.levels = levels;
        if (bids.length != levels || asks.length != levels) {
            throw new IllegalArgumentException();
        }
        this.bids = bids;
        this.asks = asks;
    }

    public int levels() {
        return levels;
    }

    public Price getBid(int index) {
        return bids[index];
    }

    public Price getAsk(int index) {
        return asks[index];
    }

}
