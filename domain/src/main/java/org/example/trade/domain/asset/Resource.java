package org.example.trade.domain.asset;

import engineering.ericdeng.architecture.domain.model.annotation.Entity;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.TradeSide;

@Entity
public abstract class Resource<T> {

    private final SecurityCode securityCode;

    protected Resource(SecurityCode securityCode) {this.securityCode = securityCode;}

    public SecurityCode securityCode() {
        return securityCode;
    }

    abstract boolean consume(Deal deal);

    abstract T remain();

    abstract TradeSide usedFor();

}