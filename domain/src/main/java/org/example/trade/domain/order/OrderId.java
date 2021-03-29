package org.example.trade.domain.order;

import org.example.trade.domain.account.AccountId;

import java.time.LocalDate;
import java.util.Objects;

public class OrderId {

    private final AccountId accountId;

    private final LocalDate tradeDay;

    private final int uid;

    public OrderId(AccountId accountId, LocalDate tradeDay, int uid) {
        this.accountId = accountId;
        this.tradeDay = tradeDay;
        this.uid = uid;
    }

    public static OrderId valueOf(AccountId accountId, LocalDate tradeDay, int uid) {
        return new OrderId(accountId, tradeDay, uid);
    }

    public LocalDate tradeDay() {
        return tradeDay;
    }

    public int uid() {
        return uid;
    }

    public AccountId accountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return "OrderId" +
            "(" + accountId +
            ", " + tradeDay +
            ", '" + uid + '\'' +
            ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        OrderId orderId = (OrderId) o;
        return uid == orderId.uid && accountId.equals(orderId.accountId) && tradeDay.equals(orderId.tradeDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, tradeDay, uid);
    }

}
