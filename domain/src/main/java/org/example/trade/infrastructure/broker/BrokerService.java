package org.example.trade.infrastructure.broker;

import org.example.trade.domain.order.TradeOrder;
import org.example.trade.domain.order.TradeRequest;

public interface BrokerService {

    TradeOrder trade(TradeRequest tradeRequest);

    void register(BrokerCallbackHandler brokerCallbackHandler);

}
