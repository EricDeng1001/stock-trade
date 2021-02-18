package org.example.trade.domain.trade;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.Asset;
import org.example.trade.domain.market.Money;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.RegularizedShares;
import org.example.trade.domain.market.StockCode;
import org.example.trade.domain.tradeorder.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class TradeTest {

    Mock mock = new Mock();

    TradeService tradeService = mock;

    LogBrokerCallbackHandler brokerCallbackHandler = new LogBrokerCallbackHandler();

    TradeRequest tradeRequest;

    RegularizedShares shares = new RegularizedShares(1000);

    StockCode stockCode = new StockCode("000001.SZ");

    Asset.Builder builder = Asset.Builder.anAsset()
        .withLockedCash(Money.ZERO)
        .withPositions(Map.of(stockCode, shares))
        .withUsableCash(new Money(1000));

    Account testAccount = new Account(mock.broker(), "testAccount", "password", builder);

    @Test
    @DisplayName("交易领域模型接口联通性测试")
    void tradeTest() {
        DomainEventBus.instance().subscribe(brokerCallbackHandler);
        tradeRequest = new LimitedPriceTradeRequest(
            stockCode,
            shares,
            TradeSide.BUY,
            new Price(1));
        TradeOrder x = tradeService.applyTo(tradeRequest, testAccount);
        mock.shutdown();
        System.out.println(x);
    }

}