package org.example.trade.domain.trade;

import org.example.trade.domain.tradeorder.TradeOrder;
import org.example.trade.domain.tradeorder.TradeOrderRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TOR implements TradeOrderRepository {

    private final Map<TradeOrder.Id, TradeOrder> orderMap = new ConcurrentHashMap<>();

    @Override
    public TradeOrder findById(TradeOrder.Id id) {
        return orderMap.get(id);
    }

    @Override
    public void save(TradeOrder order) {
        orderMap.put(order.id(), order);
    }

}
