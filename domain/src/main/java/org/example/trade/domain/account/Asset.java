package org.example.trade.domain.account;

import org.example.trade.domain.market.Money;
import org.example.trade.domain.market.StockCode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Asset {

    private final Id id;

    private final Map<StockCode, Position> positions;

    private Money usableCash;

    private Money lockedCash;
    // TODO 完善持仓部分

    public Asset(Id id, Money lockedCash,
                 Map<StockCode, Position> positions, Money usableCash) {
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

    public Map<StockCode, Position> positions() {
        return positions;
    }

    public Id id() {
        return id;
    }

    public Account account() {
        return id.account;
    }

    public static class Id {

        private final Account account;

        public Id(@NotNull Account account) {
            this.account = account;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            return account.equals(((Id) o).account);
        }

        @Override
        public int hashCode() {
            return account.hashCode();
        }

    }

}
