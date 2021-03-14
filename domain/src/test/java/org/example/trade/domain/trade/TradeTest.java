package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.Asset;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TradeTest {

    MockTradeService mockTradeService = new MockTradeService(new TOR(), new AR());

    OrderFactory tradeService = mockTradeService;

    LogBrokerCallbackHandler brokerCallbackHandler = new LogBrokerCallbackHandler();

    TradeRequest tradeRequest;

    Shares shares = new Shares(1000);

    SecurityCode securityCode = new SecurityCode("000001.SZ");

    Asset.Builder builder = Asset.Builder.anAsset()
        .withUsablePositions(new ConcurrentHashMap<>(Map.of(securityCode, shares)))
        .withUsableCash(new Money(1000));

    Account testAccount = new Account(mockTradeService.broker(), "testAccount", "password", builder);

    @Test
    @DisplayName("交易领域模型接口联通性测试")
    void tradeTest() throws TradeCantNotBeDoneException {
        DomainEventBus.instance().subscribe(brokerCallbackHandler);
        tradeRequest = new LimitedPriceTradeRequest(
            securityCode,
            shares,
            TradeSide.BUY,
            new Price(1));
        Order x = tradeService.applyTo(tradeRequest, testAccount);
        mockTradeService.shutdown();
        System.out.println(x);
    }

}