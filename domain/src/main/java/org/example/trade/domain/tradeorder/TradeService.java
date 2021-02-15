package org.example.trade.domain.tradeorder;

import org.example.trade.domain.account.Account;

import java.util.UUID;

public abstract class TradeService {

    /**
     * start a Trade from a TradeRequest, and publish TradeResult to registered TradeHandler
     * when TradeOrder get traded, publish StateChange
     *
     * @param tradeRequest a TradeRequest
     * @return a new TradeOrder from TradeRequest with no TradeResult and pending state
     */
    public TradeOrder applyTo(TradeRequest tradeRequest, Account account) {
        // TODO 风险控制
        String internalId = UUID.randomUUID().toString();
        TradeOrder order = new TradeOrder(account.id(), internalId, tradeRequest);
        if (startTrade(order)) {
            order.startTrading();
        }
        return order;
    }

    /**
     * 开始交易订单，这一步可能会失败，失败时返回false
     *
     * @param order 要交易的订单
     * @return 是否开始
     */
    protected abstract boolean startTrade(TradeOrder order);

}
