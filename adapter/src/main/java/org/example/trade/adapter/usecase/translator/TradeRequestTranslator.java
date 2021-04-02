package org.example.trade.adapter.usecase.translator;

import org.example.finance.domain.Price;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.TradeSide;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.MarketTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.interfaces.order.CreateOrderCommand;

public class TradeRequestTranslator {

    private TradeRequestTranslator() {}

    public static TradeRequest from(CreateOrderCommand command) {
        String stockCode = command.getStockCode();
        long shares = command.getShares();
        String price = command.getPrice();
        if (price == null || price.isEmpty()) {
            return new MarketTradeRequest(
                SecurityCode.valueOf(stockCode),
                Shares.valueOf(Math.abs(shares)),
                shares < 0 ? TradeSide.SELL : TradeSide.BUY
            );
        }
        return new LimitedPriceTradeRequest(
            SecurityCode.valueOf(stockCode),
            Shares.valueOf(Math.abs(shares)),
            shares < 0 ? TradeSide.SELL : TradeSide.BUY,
            Price.valueOf(price)
        );
    }

}
