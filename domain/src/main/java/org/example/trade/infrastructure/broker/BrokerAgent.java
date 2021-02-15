package org.example.trade.infrastructure.broker;

import org.example.trade.domain.order.TradeOrder;

public interface BrokerAgent {

    boolean sendOrder(TradeOrder tradeRequest);

}
