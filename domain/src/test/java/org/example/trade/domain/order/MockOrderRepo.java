package org.example.trade.domain.order;

import org.example.trade.domain.MockRepo;

public class MockOrderRepo<T, R> extends MockRepo<OrderId, Order> {

    @Override
    protected OrderId getId(Order r) {
        return r.id();
    }

}
