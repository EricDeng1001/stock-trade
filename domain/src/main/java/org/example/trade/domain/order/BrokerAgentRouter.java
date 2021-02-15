package org.example.trade.domain.order;

import org.example.trade.domain.market.Broker;
import org.example.trade.infrastructure.broker.BrokerAgent;

public interface BrokerAgentRouter {

    BrokerAgent get(Broker broker);

}
