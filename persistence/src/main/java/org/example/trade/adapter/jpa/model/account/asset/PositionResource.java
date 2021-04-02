package org.example.trade.adapter.jpa.model.account.asset;

import javax.persistence.Embeddable;
import java.math.BigInteger;

@Embeddable
public class PositionResource extends Resource {

    private String stockCode;

    private BigInteger shares;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigInteger getShares() {
        return shares;
    }

    public void setShares(BigInteger shares) {
        this.shares = shares;
    }

}
