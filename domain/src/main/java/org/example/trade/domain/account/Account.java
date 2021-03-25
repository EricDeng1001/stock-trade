package org.example.trade.domain.account;

import engineering.ericdeng.architecture.domain.model.annotation.AggregateRoot;
import org.example.trade.domain.market.Broker;

@AggregateRoot
public class Account {

    private final AccountId id;

    private String config;

    private boolean activated;

    public Account(AccountId id, String config, boolean activated) {
        this.id = id;
        this.config = config;
        this.activated = activated;
    }

    public Account(AccountId id, String config) {
        this(id, config, false);
    }

    public String config() {
        return config;
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

    public void changeConfig(String config) {
        this.config = config;
    }

    public void activate() {
        activated = true;
    }

    public void deactivate() {
        activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

}
