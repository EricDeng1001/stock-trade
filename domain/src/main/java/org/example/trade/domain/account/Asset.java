package org.example.trade.domain.account;

import org.example.trade.domain.market.Money;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.market.StockCode;

import java.util.Map;

public class Asset {

    private final Account.Id id;

    private final Map<StockCode, Shares> positions;

    private Money usableCash;

    private Money lockedCash;

    Asset(Account.Id id, Money lockedCash,
          Map<StockCode, Shares> positions, Money usableCash) {
        this.id = id;
        this.lockedCash = lockedCash;
        this.positions = positions;
        this.usableCash = usableCash;
    }

    public Money usableCash() {
        return usableCash;
    }

    public Money lockedCash() {
        return lockedCash;
    }

    /**
     * 锁定一部分现金，使得它们不再可以被使用为其他目的
     *
     * @param amount 要锁定的数量
     * @return 是否锁定成功，如果需要的数量小于可用的现金，则可以锁定，反之不行
     */
    public synchronized boolean lockCash(Money amount) {
        if (usableCash.compareTo(amount) < 0) { return false; }
        lockedCash = lockedCash.add(amount);
        usableCash = usableCash.subtract(amount);
        return true;
    }

    public Map<StockCode, Shares> positions() {
        return positions;
    }

    public Account.Id id() {
        return id;
    }

    public static final class Builder {

        private Account.Id id;

        private Map<StockCode, Shares> positions;

        private Money usableCash;

        private Money lockedCash;

        private Builder() {}

        public static Builder anAsset() { return new Builder(); }

        public Builder withId(Account.Id id) {
            this.id = id;
            return this;
        }

        public Builder withPositions(Map<StockCode, Shares> positions) {
            this.positions = positions;
            return this;
        }

        public Builder withUsableCash(Money usableCash) {
            this.usableCash = usableCash;
            return this;
        }

        public Builder withLockedCash(Money lockedCash) {
            this.lockedCash = lockedCash;
            return this;
        }

        public Asset build() { return new Asset(id, lockedCash, positions, usableCash); }

    }

}
