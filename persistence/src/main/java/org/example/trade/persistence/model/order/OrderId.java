package org.example.trade.persistence.model.order;

import org.example.trade.persistence.model.account.AccountId;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class OrderId implements Serializable {

    private AccountId accountId;

    private LocalDate tradeDay;

    private int orderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        OrderId idRecord = (OrderId) o;
        return orderId == idRecord.orderId && accountId.equals(idRecord.accountId) && tradeDay
            .equals(idRecord.tradeDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, tradeDay, orderId);
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

    public LocalDate getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(LocalDate tradeDay) {
        this.tradeDay = tradeDay;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int id) {
        this.orderId = id;
    }

    @Override
    public String toString() {
        return "OrderId{" +
            "orderId=" + orderId +
            '}';
    }

}
