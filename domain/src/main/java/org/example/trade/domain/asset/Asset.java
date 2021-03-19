package org.example.trade.domain.asset;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.annotation.AggregateRoot;
import org.example.finance.domain.Money;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@AggregateRoot
public class Asset {

    private final AccountId id;

    private final Map<SecurityCode, Shares> usablePositions;

    private final Map<OrderId, Resource<?>> allocatedResources;

    private Money usableCash;

    public Asset(AccountId id,
                 Map<SecurityCode, Shares> usablePositions,
                 Map<OrderId, Resource<?>> allocatedResources,
                 Money usableCash) {
        this.id = id;
        this.usablePositions = defaultEmpty(usablePositions);
        this.allocatedResources = defaultEmpty(allocatedResources);
        this.usableCash = Optional.ofNullable(usableCash).orElse(Money.ZERO);
    }

    public Map<SecurityCode, Shares> usablePositions() {
        return usablePositions;
    }

    public Money usableCash() {
        return usableCash;
    }

    public AccountId id() {
        return id;
    }

    public boolean canAllocate(Money amount) {
        return usableCash.compareTo(amount) >= 0;
    }

    public boolean canAllocate(SecurityCode securityCode, Shares amount) {
        Shares usableShares = usablePositions.get(securityCode);
        return usableShares != null && usableShares.compareTo(amount) >= 0;
    }

    public boolean consume(OrderId id, Deal deal) {
        Resource<?> resource = resourceOf(id);
        switch (resource.usedFor()) {
            case BUY -> gain(resource.securityCode(), deal.shares());
            case SELL -> gain(deal.value());
        }
        return resource.consume(deal);
    }

    public Resource<?> resourceOf(OrderId order) {
        return allocatedResources.get(order);
    }

    public void reclaim(OrderId id) {
        Resource<?> resource = resourceOf(id);
        switch (resource.usedFor()) {
            case BUY -> {
                Money m = (Money) resource.remain();
                gain(m);
            }
            case SELL -> {
                Shares shares = (Shares) resource.remain();
                gain(resource.securityCode(), shares);
            }
        }
        removeResource(id);
    }

    public void gain(Money amount) {
        if (amount.compareTo(Money.ZERO) == 0) { return; }
        usableCash = usableCash.add(amount);
        DomainEventBus.instance().publish(
            new AssetGainCashed(Instant.now(), id, amount, usableCash)
        );
    }

    public void gain(SecurityCode securityCode, Shares shares) {
        if (shares.compareTo(Shares.ZERO) == 0) { return; }
        Shares x = usablePositions.get(securityCode);
        if (x == null) {
            usablePositions.put(securityCode, shares);
            x = shares;
        } else {
            x = x.add(shares);
            usablePositions.put(securityCode, x);
        }
        DomainEventBus.instance().publish(
            new AssetGainPositioned(Instant.now(), id, securityCode, shares, x)
        );
    }

    public Resource<?> tryAllocate(Order order) {
        return switch (order.requirement().tradeSide()) {
            case BUY -> tryAllocate(order.id(), order.requirement().securityCode(), order.requirement().value());
            case SELL -> tryAllocate(order.id(), order.requirement().securityCode(), order.requirement().shares());
        };
    }

    /**
     * 锁定一部分现金，使得它们不再可以被使用为其他目的
     *
     * @param securityCode 要分配的股票
     * @param amount       要锁定的数量
     */
    private CashResource tryAllocate(OrderId order, SecurityCode securityCode, Money amount) {
        if (canAllocate(amount)) {
            return allocate(order, securityCode, amount);
        }
        return null;
    }

    private PositionResource tryAllocate(OrderId order, SecurityCode securityCode, Shares amount) {
        if (canAllocate(securityCode, amount)) {
            return allocate(order, securityCode, amount);
        }
        return null;
    }

    @NotNull
    private CashResource allocate(OrderId order, SecurityCode securityCode, Money amount) {
        CashResource cashResource = new CashResource(securityCode, amount);
        usableCash = usableCash.subtract(amount);
        allocatedResources.put(order, cashResource);
        DomainEventBus.instance().publish(
            new ResourceAllocated(Instant.now(), id, order, cashResource)
        );
        return cashResource;
    }

    @NotNull
    private PositionResource allocate(OrderId order, SecurityCode securityCode, Shares amount) {
        PositionResource positionResource = new PositionResource(securityCode, amount);
        allocatedResources.put(order, positionResource);
        usablePositions.computeIfPresent(securityCode, (s, v) -> v.subtract(amount));
        DomainEventBus.instance().publish(
            new ResourceAllocated(Instant.now(), id, order, positionResource)
        );
        return positionResource;
    }

    private <T, R> Map<T, R> defaultEmpty(Map<T, R> map) {
        if (map == null) { return new ConcurrentHashMap<>(); }
        return map;
    }

    private void removeResource(OrderId id) {
        allocatedResources.remove(id);
    }

}
