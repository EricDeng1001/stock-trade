package org.example.trade.adapter.persistence.model.account;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
public class Account {

    @EmbeddedId
    private AccountId id;

    private String config;

    private boolean activated;

    public Account() {}

    public Account(org.example.trade.domain.account.AccountId id, String config, boolean activated) {
        this.id = new AccountId(id);
        this.config = config;
        this.activated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Account that = (Account) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public AccountId getId() {
        return id;
    }

    public void setId(AccountId id) {
        this.id = id;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
