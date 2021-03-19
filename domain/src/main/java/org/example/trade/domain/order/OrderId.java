package org.example.trade.domain.order;

import org.example.trade.domain.account.AccountId;

import java.time.LocalDate;

public class OrderId {

    private final AccountId accountId;

    private final LocalDate tradeDay;

    private final int uid;

    public OrderId(AccountId accountId, LocalDate tradeDay, int uid) {
        this.accountId = accountId;
        this.tradeDay = tradeDay;
        this.uid = uid;
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
        return "OrderId{" +
            "account=" + accountId +
            ", tradeDay=" + tradeDay +
            ", id='" + uid + '\'' +
            '}';
    }

}
