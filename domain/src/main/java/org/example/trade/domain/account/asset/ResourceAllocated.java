package org.example.trade.domain.account.asset;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.OrderId;

import java.time.Instant;

public class ResourceAllocated extends AssetEvent {

    private final OrderId order;

    private final Resource<?> allocatedResource;

    public ResourceAllocated(Instant occurredOn, AccountId id, OrderId order,
                             Resource<?> allocatedResource) {
        super(occurredOn, id);
        this.order = order;
        this.allocatedResource = allocatedResource;
    }

    public OrderId order() {
        return order;
    }

    public Resource<?> allocatedResource() {
        return allocatedResource;
    }

    @Override
    public String toString() {
        return "ResourceAllocated{" +
            "account=" + account +
            ", order=" + order +
            ", allocatedResource=" + allocatedResource +
            ", occurredOn=" + occurredOn +
            '}';
    }

}
