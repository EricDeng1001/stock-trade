package org.example.trade.adapter.jpa.model.account;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccountId implements Serializable {

    private String broker;

    private String accountBrokerId;

    public AccountId(org.example.trade.domain.account.AccountId id) {
        this.broker = id.broker().id();
        this.accountBrokerId = id.brokerId();
    }

    public AccountId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AccountId that = (AccountId) o;
        return broker.equals(that.broker) && accountBrokerId.equals(that.accountBrokerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(broker, accountBrokerId);
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getAccountBrokerId() {
        return accountBrokerId;
    }

    public void setAccountBrokerId(String brokerId) {
        this.accountBrokerId = brokerId;
    }

}
