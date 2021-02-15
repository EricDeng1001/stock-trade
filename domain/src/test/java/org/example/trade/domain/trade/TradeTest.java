package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;
import org.example.trade.domain.order.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TradeTest {

    Mock mock = new Mock();

    TradeService tradeService = new TradeService(mock);

    LogBrokerCallbackHandler brokerCallbackHandler = new LogBrokerCallbackHandler();

    TradeRequest tradeRequest;

    Account testAccount = new Account(mock, "testAccount");

    @Test
    @DisplayName("交易领域模型接口联通性测试")
    void tradeTest() {
        DomainEventBus.instance().subscribe(brokerCallbackHandler);
        tradeRequest = new LimitedPriceTradeRequest(
            new StockCode("000001.SZ"),
            new RegularizedShares(1000),
            TradeSide.BUY,
            new Price(1));
        TradeOrder x = tradeService.applyTo(tradeRequest, testAccount);
        mock.shutdown();
        System.out.println(x);
    }

}