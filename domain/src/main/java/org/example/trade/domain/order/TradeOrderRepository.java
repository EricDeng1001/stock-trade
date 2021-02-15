package org.example.trade.domain.order;

public interface TradeOrderRepository {

    TradeOrder findById(TradeOrder.Id id);

    void save(TradeOrder order);

}
