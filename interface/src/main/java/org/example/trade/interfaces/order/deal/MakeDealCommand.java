package org.example.trade.interfaces.order.deal;

import org.example.trade.interfaces.order.OrderIdDTO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

public class MakeDealCommand {

    private final OrderIdDTO orderId;

    private final BigInteger shares;

    private final BigDecimal price;

    private final Instant time;

    public MakeDealCommand(OrderIdDTO orderId, BigInteger shares, BigDecimal price, Instant time) {
        this.orderId = orderId;
        this.shares = shares;
        this.price = price;
        this.time = time;
    }

    public OrderIdDTO orderId() {
        return orderId;
    }

    public BigInteger shares() {
        return shares;
    }

    public BigDecimal price() {
        return price;
    }

    public Instant time() {
        return time;
    }

}
