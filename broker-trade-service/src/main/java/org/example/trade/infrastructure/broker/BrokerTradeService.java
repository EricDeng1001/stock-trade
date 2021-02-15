package org.example.trade.infrastructure.broker;

import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Price;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.tradeorder.Deal;
import org.example.trade.domain.tradeorder.TradeOrder;
import org.example.trade.domain.tradeorder.TradeOrderRepository;
import org.example.trade.domain.tradeorder.TradeService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

public abstract class BrokerTradeService extends TradeService {

    protected final TradeOrderRepository tradeOrderRepository;

    protected BrokerTradeService(TradeOrderRepository tradeOrderRepository) {
        this.tradeOrderRepository = tradeOrderRepository;
    }

    protected void handleTradeEvent(String broker, String orderId, BigInteger shares, BigDecimal price, Instant time) {
        TradeOrder.Id id = new TradeOrder.Id(new Broker(broker), orderId, true);
        TradeOrder order = tradeOrderRepository.findById(id);
        Deal deal = new Deal(
            new Shares(shares),
            new Price(price)
        );
        order.makeDeal(deal, time);
        tradeOrderRepository.save(order);
    }

}
