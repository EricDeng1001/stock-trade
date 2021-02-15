package org.example.trade.interfaces.input;

public class TradeRequest {

    private String stockCode;

    private long shares;

    private String tradeSide;

    private String priceType;

    public TradeRequest() {
    }

    public TradeRequest(String stockCode, long shares, String tradeSide, String priceType) {
        this.stockCode = stockCode;
        this.shares = shares;
        this.tradeSide = tradeSide;
        this.priceType = priceType;
    }

    public String stockCode() {
        return stockCode;
    }

    public long shares() {
        return shares;
    }

    public String tradeSide() {
        return tradeSide;
    }

    public String priceType() {
        return priceType;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public void setTradeSide(String tradeSide) {
        this.tradeSide = tradeSide;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

}
