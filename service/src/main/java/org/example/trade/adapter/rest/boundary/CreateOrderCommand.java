package org.example.trade.adapter.rest.boundary;

public class CreateOrderCommand {

    private String stockCode;

    private long shares;

    private String price;

    public String getStockCode() {
        return stockCode;
    }

    public long getShares() {
        return shares;
    }

    public String getPrice() {
        return price;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
