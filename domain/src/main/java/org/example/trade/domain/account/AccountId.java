package org.example.trade.domain.account;

import org.example.trade.domain.market.Broker;

import java.util.Objects;

public class AccountId {

    private final Broker broker;

    private final String brokerId;

    public AccountId(Broker broker, String brokerId) {
        this.broker = broker;
        this.brokerId = brokerId;
    }

    public Broker broker() {
        return broker;
    }

    public String brokerId() {
        return brokerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AccountId accountId = (AccountId) o;
        return broker.equals(accountId.broker) && brokerId.equals(accountId.brokerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(broker, brokerId);
    }

    @Override
    public String toString() {
        return "AccountId" +
            "(" + broker +
            ", '" + brokerId + '\'' +
            ')';
    }

}
