package org.example.trade.domain.tradeorder;

public interface TradeOrderRepository {

    TradeOrder findById(TradeOrder.Id id);

    void save(TradeOrder order);

}
