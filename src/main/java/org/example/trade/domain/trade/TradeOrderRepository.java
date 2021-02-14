package org.example.trade.domain.trade;

public interface TradeOrderRepository {

    TradeOrder findById(TradeOrder.Id id);

    void save(TradeOrder order);

}
