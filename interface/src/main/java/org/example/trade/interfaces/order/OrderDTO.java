package org.example.trade.interfaces.order;

import java.math.BigDecimal;

public class OrderDTO {

    private final OrderIdDTO orderId;

    private final String stockCode;

    private final long shares;

    private final BigDecimal price;

    public OrderDTO(OrderIdDTO orderId, String stockCode, long shares, BigDecimal price) {
        this.orderId = orderId;
        this.stockCode = stockCode;
        this.shares = shares;
        this.price = price;
    }

    public OrderIdDTO orderId() {
        return orderId;
    }

    public String stockCode() {
        return stockCode;
    }

    public long shares() {
        return shares;
    }

    public BigDecimal price() {
        return price;
    }

}
