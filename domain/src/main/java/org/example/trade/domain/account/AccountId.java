package org.example.trade.domain.account;

import org.example.trade.domain.market.Broker;

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
    public String toString() {
        return "Id{" +
            "broker=" + broker +
            ", brokerId='" + brokerId + '\'' +
            '}';
    }

}
