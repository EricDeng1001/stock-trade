package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;

class LogBrokerCallbackHandler extends DomainEventSubscriber<OrderTraded> {

    @Override
    public void handle(OrderTraded orderTraded) {
        System.out.println(orderTraded);
    }

}
