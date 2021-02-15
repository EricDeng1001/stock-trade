package org.example.trade.infrastructure.broker;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.trade.*;

public class BrokerAgent implements TradeService, BrokerCallbackHandler {

    private final BrokerService brokerService;

    private final TradeOrderRepository tradeOrderRepository;

    public BrokerAgent(BrokerService brokerService,
                       TradeOrderRepository tradeOrderRepository) {
        this.brokerService = brokerService;
        this.tradeOrderRepository = tradeOrderRepository;
        brokerService.register(this);
    }

    @Override
    public TradeOrder trade(TradeRequest tradeRequest) {
        return brokerService.trade(tradeRequest);
    }

    @Override
    public void onTrade(OrderTraded orderTraded) {
        TradeOrder order = tradeOrderRepository.findById(orderTraded.orderId());
        order.attachTrade(orderTraded);
        tradeOrderRepository.save(order);
        DomainEventBus.instance().publish(orderTraded);
    }

}
