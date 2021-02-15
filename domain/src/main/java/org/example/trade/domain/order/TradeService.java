package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.infrastructure.broker.BrokerAgent;

import java.util.UUID;

public class TradeService {

    private final BrokerAgentRouter brokerAgentRouter;

    public TradeService(BrokerAgentRouter brokerAgentRouter) {this.brokerAgentRouter = brokerAgentRouter;}

    /**
     * start a Trade from a TradeRequest, and publish TradeResult to registered TradeHandler
     * when TradeOrder get traded, publish StateChange
     *
     * @param tradeRequest a TradeRequest
     * @return a new TradeOrder from TradeRequest with no TradeResult and pending state
     */
    public TradeOrder applyTo(TradeRequest tradeRequest, Account account) {
        // TODO 风险控制
        String internalId = UUID.randomUUID().toString();
        TradeOrder order = new TradeOrder(account.id(), internalId, tradeRequest);
        BrokerAgent brokerAgent = brokerAgentRouter.get(account.broker());
        // TODO 增加下单失败策略
        brokerAgent.sendOrder(order);
        return order;
    }

}
