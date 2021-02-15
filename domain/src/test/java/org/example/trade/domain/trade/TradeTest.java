package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;
import org.example.trade.domain.order.*;
import org.example.trade.infrastructure.broker.BrokerAgent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TradeTest {

    MockBroker mockBroker = new MockBroker();

    TradeService tradeService = new BrokerAgent(mockBroker, mockBroker);

    LogBrokerCallbackHandler brokerCallbackHandler = new LogBrokerCallbackHandler();

    TradeRequest tradeRequest;

    Account testAccount = new Account(mockBroker, "testAccount");

    @Test
    @DisplayName("交易领域模型接口联通性测试")
    void tradeTest() {
        DomainEventBus.instance().subscribe(brokerCallbackHandler);
        tradeRequest = new LimitedPriceTradeRequest(
            new StockCode("000001.SZ"),
            new RegularizedShares(1000),
            TradeSide.BUY,
            new Price(1), testAccount);
        TradeOrder x = tradeService.trade(tradeRequest);
        mockBroker.shutdown();
        System.out.println(x);
    }

}