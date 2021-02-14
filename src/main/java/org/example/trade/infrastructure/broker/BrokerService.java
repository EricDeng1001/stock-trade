package org.example.trade.infrastructure.broker;

import org.example.trade.domain.trade.TradeOrder;
import org.example.trade.domain.trade.TradeRequest;

public interface BrokerService {

    TradeOrder trade(TradeRequest tradeRequest);

    void register(BrokerCallbackHandler brokerCallbackHandler);

}
