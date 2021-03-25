package org.example.trade.adapter.persistence.model.account.asset;

import org.example.trade.adapter.persistence.model.account.AccountId;
import org.example.trade.adapter.persistence.model.order.OrderId;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@Entity
public class Asset {

    @EmbeddedId
    private AccountId accountId;

    private BigDecimal usableCash;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, BigInteger> usablePositions;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<OrderId, CashResource> cashResources;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<OrderId, PositionResource> positionResources;

    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getUsableCash() {
        return usableCash;
    }

    public void setUsableCash(BigDecimal usableCash) {
        this.usableCash = usableCash;
    }

    public Map<String, BigInteger> getUsablePositions() {
        return usablePositions;
    }

    public void setUsablePositions(Map<String, BigInteger> usablePositions) {
        this.usablePositions = usablePositions;
    }

    public Map<OrderId, CashResource> getCashResources() {
        return cashResources;
    }

    public void setCashResources(
        Map<OrderId, CashResource> cashResources) {
        this.cashResources = cashResources;
    }

    public Map<OrderId, PositionResource> getPositionResources() {
        return positionResources;
    }

    public void setPositionResources(
        Map<OrderId, PositionResource> positionResources) {
        this.positionResources = positionResources;
    }

}
