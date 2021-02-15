package org.example.trade.infrastructure.broker;

import org.example.trade.domain.trade.OrderTraded;

public interface BrokerCallbackHandler {

    void onTrade(OrderTraded orderTraded);

}
