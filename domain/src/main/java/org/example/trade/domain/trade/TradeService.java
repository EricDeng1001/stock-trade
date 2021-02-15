package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEventPublisher;

public interface TradeService extends DomainEventPublisher<OrderTraded> {

    /**
     * start a Trade from a TradeRequest, and publish TradeResult to registered TradeHandler
     * when TradeOrder get traded, publish StateChange
     *
     * @param tradeRequest a TradeRequest
     * @return a new TradeOrder from TradeRequest with no TradeResult and pending state
     */
    TradeOrder trade(TradeRequest tradeRequest);

}
