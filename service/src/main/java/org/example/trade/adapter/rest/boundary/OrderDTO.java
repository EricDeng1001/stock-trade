package org.example.trade.adapter.rest.boundary;

import java.util.List;

public class OrderDTO {

    private String orderId;

    private String stockCode;

    private String shares;

    private String price;

    private List<TradeDTO> trades;

    private String status;

    public OrderDTO() {
    }

    public OrderDTO(String orderId, String stockCode, String shares, String price, List<TradeDTO> dtos, String status) {
        this.orderId = orderId;
        this.stockCode = stockCode;
        this.shares = shares;
        this.price = price;
        this.trades = dtos;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TradeDTO> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeDTO> trades) {
        this.trades = trades;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
