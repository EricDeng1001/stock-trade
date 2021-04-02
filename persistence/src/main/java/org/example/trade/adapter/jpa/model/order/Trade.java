package org.example.trade.adapter.jpa.model.order;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@Embeddable
public class Trade {

    private BigDecimal price;

    private BigInteger shares;

    private String tradeBrokerId;

    private Instant dealtOn;

    public Instant getDealtOn() {
        return dealtOn;
    }

    public void setDealtOn(Instant dealtOn) {
        this.dealtOn = dealtOn;
    }

    public String getTradeBrokerId() {
        return tradeBrokerId;
    }

    public void setTradeBrokerId(String brokerId) {
        this.tradeBrokerId = brokerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigInteger getShares() {
        return shares;
    }

    public void setShares(BigInteger shares) {
        this.shares = shares;
    }

}
