package org.example.trade.domain.account;

import org.example.trade.domain.market.Money;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.tradeorder.TradeOrder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Asset {

    private final Account.Id id;

    private final Map<SecurityCode, Shares> usablePositions;

    private final Map<SecurityCode, Shares> lockedPositions;

    private final Map<AssetLocker, CashLock> cashLocks;

    private final Map<AssetLocker, SharesLock> sharesLocks;

    private Money usableCash;

    private Money lockedCash;

    private Asset(Account.Id id,
                  Map<SecurityCode, Shares> usablePositions,
                  Map<SecurityCode, Shares> lockedPositions,
                  Map<AssetLocker, CashLock> cashLocks,
                  Map<AssetLocker, SharesLock> sharesLocks,
                  Money usableCash, Money lockedCash) {
        this.id = id;
        this.usablePositions = Optional.ofNullable(usablePositions).orElse(new ConcurrentHashMap<>());
        this.lockedPositions = Optional.ofNullable(lockedPositions).orElse(new ConcurrentHashMap<>());
        this.cashLocks = Optional.ofNullable(cashLocks).orElse(new ConcurrentHashMap<>());
        this.sharesLocks = Optional.ofNullable(sharesLocks).orElse(new ConcurrentHashMap<>());
        this.usableCash = Optional.ofNullable(usableCash).orElse(Money.ZERO);
        this.lockedCash = Optional.ofNullable(lockedCash).orElse(Money.ZERO);
    }

    public Map<SecurityCode, Shares> usablePositions() {
        return usablePositions;
    }

    public Map<SecurityCode, Shares> lockedPositions() {
        return lockedPositions;
    }

    public Money usableCash() {
        return usableCash;
    }

    public Money lockedCash() {
        return lockedCash;
    }

    public Account.Id id() {
        return id;
    }

    public synchronized boolean cantLock(Money amount) {
        return usableCash.compareTo(amount) < 0;
    }

    public synchronized boolean cantLock(SecurityCode securityCode, Shares amount) {
        Shares usableShares = usablePositions.get(securityCode);
        return usableShares == null || usableShares.compareTo(amount) < 0;
    }

    /**
     * 锁定一部分现金，使得它们不再可以被使用为其他目的
     *
     * @param amount 要锁定的数量
     * @return 包含此处分配资金的CashLocker对象，如果需要的数量小于可用的现金，则可以锁定，反之返回null
     */
    public synchronized CashLock lockCash(Money amount, AssetLocker assetLocker) {
        if (cantLock(amount)) { return null; }
        CashLock cashLock = new CashLock(amount);
        cashLocks.put(assetLocker, cashLock);
        return cashLock;
    }

    public synchronized SharesLock lockShares(SecurityCode securityCode, RegularizedShares shares,
                                              AssetLocker assetLocker) {
        if (cantLock(securityCode, shares)) { return null; }
        SharesLock sharesLock = new SharesLock(securityCode, shares);
        sharesLocks.put(assetLocker, sharesLock);
        return sharesLock;
    }

    public CashLock getCashLock(TradeOrder tradeOrder) {
        return cashLocks.get(tradeOrder);
    }

    public SharesLock getSharesLock(TradeOrder tradeOrder) {
        return sharesLocks.get(tradeOrder);
    }

    public final class CashLock {

        private Money amount;

        private boolean disposed;

        private CashLock(Money amount) {
            this.amount = amount;
            lockedCash = lockedCash.add(amount);
            usableCash = usableCash.subtract(amount);
            disposed = false;
        }

        public Money amount() {
            return amount;
        }

        public synchronized void consume(Money amount) {
            if (amount.compareTo(this.amount) > 0) { throw new IllegalStateException(); }
            this.amount = this.amount.subtract(amount);
            lockedCash = lockedCash.subtract(amount);
        }

        public synchronized void dispose() {
            if (disposed) { throw new IllegalStateException(); }
            disposed = true;
            lockedCash = lockedCash.subtract(amount);
            usableCash = usableCash.add(amount);
        }

    }

    public final class SharesLock {

        private final SecurityCode securityCode;

        private Shares amount;

        private boolean disposed;

        private SharesLock(SecurityCode securityCode, RegularizedShares amount) {
            this.securityCode = securityCode;
            this.amount = amount;
            lockedPositions.merge(securityCode, amount, (s, v) -> v.add(amount));
            usablePositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
        }

        public Shares amount() {
            return amount;
        }

        public synchronized void consume(Shares amount) {
            if (amount.compareTo(this.amount) > 0) { throw new IllegalStateException(); }
            this.amount = this.amount.subtract(amount);
            lockedPositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
        }

        public synchronized void dispose() {
            if (disposed) { throw new IllegalStateException(); }
            disposed = true;
            lockedPositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
            usablePositions.computeIfPresent(securityCode, (s, v) -> v.add(amount));
        }

    }

    public static final class Builder {

        private Account.Id id;

        private Map<SecurityCode, Shares> usablePositions;

        private Map<SecurityCode, Shares> lockedPositions;

        private Map<AssetLocker, CashLock> cashLocks;

        private Map<AssetLocker, SharesLock> sharesLocks;

        private Money usableCash;

        private Money lockedCash;

        private Builder() {}

        public static Builder anAsset() { return new Builder(); }

        public Builder withId(Account.Id id) {
            this.id = id;
            return this;
        }

        public Builder withUsablePositions(Map<SecurityCode, Shares> usablePositions) {
            this.usablePositions = usablePositions;
            return this;
        }

        public Builder withLockedPositions(Map<SecurityCode, Shares> lockedPositions) {
            this.lockedPositions = lockedPositions;
            return this;
        }

        public Builder withCashLocks(Map<AssetLocker, CashLock> cashLocks) {
            this.cashLocks = cashLocks;
            return this;
        }

        public Builder withSharesLocks(Map<AssetLocker, SharesLock> sharesLocks) {
            this.sharesLocks = sharesLocks;
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

        public Asset build() {
            return new Asset(id, usablePositions, lockedPositions, cashLocks, sharesLocks, usableCash, lockedCash);
        }

    }

}
