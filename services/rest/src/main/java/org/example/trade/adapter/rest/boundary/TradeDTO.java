package org.example.trade.adapter.rest.boundary;

import java.time.Instant;

public class TradeDTO {

    private String price;

    private String shares;

    private Instant time;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

}
