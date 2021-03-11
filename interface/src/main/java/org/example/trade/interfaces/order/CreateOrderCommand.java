package org.example.trade.interfaces.order;

import java.math.BigDecimal;

public class CreateOrderCommand {

    private final String stockCode;

    private final long shares;

    private final BigDecimal price;

    public CreateOrderCommand(String stockCode, long shares, BigDecimal price) {
        this.stockCode = stockCode;
        this.shares = shares;
        this.price = price;
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
