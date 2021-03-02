package org.example.trade.application;

import org.example.finance.domain.Price;
import org.example.trade.domain.market.Broker;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.tradeorder.Deal;
import org.example.trade.domain.tradeorder.TradeOrder;
import org.example.trade.domain.tradeorder.TradeOrderRepository;
import org.example.trade.interfaces.DealService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

public class DealServiceImpl implements DealService {

    private final TradeOrderRepository tradeOrderRepository;

    private final org.example.trade.domain.tradeorder.DealService dealService;

    public DealServiceImpl(TradeOrderRepository tradeOrderRepository,
                           org.example.trade.domain.tradeorder.DealService dealService) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.dealService = dealService;
    }

    @Override
    public void makeDeal(String broker, String orderId, BigInteger shares, BigDecimal price, Instant time) {
        TradeOrder.Id id = new TradeOrder.Id(new Broker(broker), orderId, true);
        TradeOrder order = tradeOrderRepository.findById(id);
        Deal deal = new Deal(
            new Shares(shares),
            new Price(price)
        );
        dealService.makeDeal(order, deal, time);
        tradeOrderRepository.save(order);
    }

    @Override
    public void finishOrder(String broker, String orderId) {
        TradeOrder.Id id = new TradeOrder.Id(new Broker(broker), orderId, true);
        TradeOrder order = tradeOrderRepository.findById(id);
        dealService.finishOrder(order);
        tradeOrderRepository.save(order);
    }

}
