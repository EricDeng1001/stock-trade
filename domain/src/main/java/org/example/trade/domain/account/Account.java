package org.example.trade.domain.account;

import engineering.ericdeng.architecture.domain.model.annotation.AggregateRoot;
import org.example.trade.domain.market.Broker;

@AggregateRoot
public class Account {

    private final AccountId id;

    private String password;

    public Account(Broker broker, String brokerId, String password) {
        this.id = new AccountId(broker, brokerId);
        this.password = password;
    }

    public String password() {
        return password;
    }

    public AccountId id() {
        return id;
    }

    public Broker broker() {
        return id.broker();
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            '}';
    }

    public void changePassword(String password) {
        this.password = password;
    }

}
