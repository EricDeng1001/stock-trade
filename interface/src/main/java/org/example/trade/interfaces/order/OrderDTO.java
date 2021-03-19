package org.example.trade.interfaces.order;

public class OrderDTO {

    private final String orderId;

    private final String stockCode;

    private final String shares;

    private final String price;

    public OrderDTO(String orderId, String stockCode, String shares, String price) {
        this.orderId = orderId;
        this.stockCode = stockCode;
        this.shares = shares;
        this.price = price;
    }

    public String orderId() {
        return orderId;
    }

    public String stockCode() {
        return stockCode;
    }

    public String shares() {
        return shares;
    }

    public String price() {
        return price;
    }

}
