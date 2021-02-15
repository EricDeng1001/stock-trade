package org.example.trade.domain.market;

public class Stock {

    private final StockCode stockCode;

    private final Price currentPrice;

    private final Price todayOpenPrice;

    private final Price yesterdayClosePrice;

    private final FiveLevelPrice fiveLevelPrice;

    private final Shares tradeVolume;

    public Stock(StockCode stockCode, Price currentPrice, Price todayOpenPrice,
                 Price yesterdayClosePrice, FiveLevelPrice fiveLevelPrice,
                 Shares tradeVolume) {
        this.stockCode = stockCode;
        this.currentPrice = currentPrice;
        this.todayOpenPrice = todayOpenPrice;
        this.yesterdayClosePrice = yesterdayClosePrice;
        this.fiveLevelPrice = fiveLevelPrice;
        this.tradeVolume = tradeVolume;
    }

    public StockCode stockCode() {
        return stockCode;
    }

    public Price currentPrice() {
        return currentPrice;
    }

    public Price todayOpenPrice() {
        return todayOpenPrice;
    }

    public Price yesterdayClosePrice() {
        return yesterdayClosePrice;
    }

    public FiveLevelPrice fiveLevelPrice() {
        return fiveLevelPrice;
    }

    public Shares tradeVolume() {
        return tradeVolume;
    }

}
