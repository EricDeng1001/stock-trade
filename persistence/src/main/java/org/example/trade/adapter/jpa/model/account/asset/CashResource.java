package org.example.trade.adapter.jpa.model.account.asset;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class CashResource extends Resource {

    private String stockCode;

    private BigDecimal money;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

}
