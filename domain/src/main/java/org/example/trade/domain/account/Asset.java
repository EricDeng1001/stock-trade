package org.example.trade.domain.account;

import org.example.finance.domain.Money;
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

    private final Map<AssetLocker, CashResource> cashLocks;

    private final Map<AssetLocker, SharesResource> sharesLocks;

    private Money usableCash;

    private Money lockedCash;

    private Asset(Account.Id id,
                  Map<SecurityCode, Shares> usablePositions,
                  Map<SecurityCode, Shares> lockedPositions,
                  Map<AssetLocker, CashResource> cashLocks,
                  Map<AssetLocker, SharesResource> sharesLocks,
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
    public CashResource lockCash(Money amount, AssetLocker assetLocker) {
        if (cantLock(amount)) { return null; }
        CashResource cashResource = new CashResource(amount);
        cashLocks.put(assetLocker, cashResource);
        return cashResource;
    }

    public SharesResource lockShares(SecurityCode securityCode, Shares shares,
                                     AssetLocker assetLocker) {
        if (cantLock(securityCode, shares)) { return null; }
        SharesResource sharesResource = new SharesResource(securityCode, shares);
        sharesLocks.put(assetLocker, sharesResource);
        return sharesResource;
    }

    public void gainCash(Money amount) {
        usableCash = usableCash.add(amount);
    }

    public void gainShares(SecurityCode securityCode, Shares shares) {
        usablePositions.computeIfPresent(securityCode, (s, v) -> v.add(shares));
    }

    public CashResource getCashLock(TradeOrder tradeOrder) {
        return cashLocks.get(tradeOrder);
    }

    public SharesResource getSharesLock(TradeOrder tradeOrder) {
        return sharesLocks.get(tradeOrder);
    }

    public void deleteEmptyResource() {

    }

    public final class CashResource {

        private Money amount;

        private boolean isEmpty;

        private CashResource(Money amount) {
            this.amount = amount;
            lockedCash = lockedCash.add(amount);
            usableCash = usableCash.subtract(amount);
            isEmpty = false;
        }

        public Money amount() {
            return amount;
        }

        public void consume(Money amount) {
            if (amount.compareTo(this.amount) > 0) { throw new IllegalStateException(); }
            this.amount = this.amount.subtract(amount);
            lockedCash = lockedCash.subtract(amount);
        }

        public void clear() {
            if (isEmpty) { throw new IllegalStateException(); }
            isEmpty = true;
            lockedCash = lockedCash.subtract(amount);
            usableCash = usableCash.add(amount);
        }

    }

    public final class SharesResource {

        private final SecurityCode securityCode;

        private Shares amount;

        private boolean isEmpty;

        private SharesResource(SecurityCode securityCode, Shares amount) {
            this.securityCode = securityCode;
            this.amount = amount;
            lockedPositions.merge(securityCode, amount, (s, v) -> v.add(amount));
            usablePositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
        }

        public Shares amount() {
            return amount;
        }

        public void consume(Shares amount) {
            if (amount.compareTo(this.amount) > 0) { throw new IllegalStateException(); }
            this.amount = this.amount.subtract(amount);
            lockedPositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
        }

        public void clear() {
            if (isEmpty) { throw new IllegalStateException(); }
            isEmpty = true;
            lockedPositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
            usablePositions.computeIfPresent(securityCode, (s, v) -> v.add(amount));
        }

    }

    public static final class Builder {

        private Account.Id id;

        private Map<SecurityCode, Shares> usablePositions;

        private Map<SecurityCode, Shares> lockedPositions;

        private Map<AssetLocker, CashResource> cashLocks;

        private Map<AssetLocker, SharesResource> sharesLocks;

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

        public Builder withCashLocks(Map<AssetLocker, CashResource> cashLocks) {
            this.cashLocks = cashLocks;
            return this;
        }

        public Builder withSharesLocks(Map<AssetLocker, SharesResource> sharesLocks) {
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
