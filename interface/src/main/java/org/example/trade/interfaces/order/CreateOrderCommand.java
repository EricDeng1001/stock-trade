package org.example.trade.interfaces.order;

public class CreateOrderCommand {

    private String stockCode;

    private long shares;

    private String price;

    private String accountId;

    public String getStockCode() {
        return stockCode;
    }

    public long getShares() {
        return shares;
    }

    public String getPrice() {
        return price;
    }

    public String getAccountId() {
        return accountId;
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

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
