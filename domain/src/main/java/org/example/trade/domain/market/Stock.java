package org.example.trade.domain.market;

import org.example.finance.domain.Price;

public class Stock {

    private final SecurityCode securityCode;

    private final Price lastPrice;

    private final Price todayOpenPrice;

    private final Price lastClosePrice;

    private final LevelPrice levelPrice;

    private final Shares tradeVolume;

    public Stock(SecurityCode securityCode, Price lastPrice, Price todayOpenPrice,
                 Price lastClosePrice, LevelPrice levelPrice,
                 Shares tradeVolume) {
        this.securityCode = securityCode;
        this.lastPrice = lastPrice;
        this.todayOpenPrice = todayOpenPrice;
        this.lastClosePrice = lastClosePrice;
        this.levelPrice = levelPrice;
        this.tradeVolume = tradeVolume;
    }

    public SecurityCode stockCode() {
        return securityCode;
    }

    public Price currentPrice() {
        return lastPrice;
    }

    public Price todayOpenPrice() {
        return todayOpenPrice;
    }

    public Price yesterdayClosePrice() {
        return lastClosePrice;
    }

    public LevelPrice fiveLevelPrice() {
        return levelPrice;
    }

    public Shares tradeVolume() {
        return tradeVolume;
    }

    @Override
    public String toString() {
        return "Stock{" +
            "stockCode=" + securityCode +
            ", currentPrice=" + lastPrice +
            ", todayOpenPrice=" + todayOpenPrice +
            ", yesterdayClosePrice=" + lastClosePrice +
            ", fiveLevelPrice=" + levelPrice +
            ", tradeVolume=" + tradeVolume +
            '}';
    }

}
