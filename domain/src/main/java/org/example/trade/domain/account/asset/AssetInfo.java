package org.example.trade.domain.account.asset;

import engineering.ericdeng.architecture.domain.model.annotation.ValueObject;
import org.example.finance.domain.Money;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;

import java.util.Map;

@ValueObject
public class AssetInfo {

    private final Map<SecurityCode, Shares> usablePositions;

    private final Money usableCash;

    public AssetInfo(
        Map<SecurityCode, Shares> usablePositions, Money usableCash) {
        this.usablePositions = usablePositions;
        this.usableCash = usableCash;
    }

    public Map<SecurityCode, Shares> usablePositions() {
        return usablePositions;
    }

    public Money usableCash() {
        return usableCash;
    }

}
