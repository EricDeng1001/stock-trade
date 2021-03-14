package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.account.Asset;

import java.util.UUID;

public class OrderFactory {

    protected final OrderRepository orderRepository;

    protected final AccountRepository accountRepository;

    public OrderFactory(OrderRepository orderRepository,
                        AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * start a Trade from a TradeRequest, and publish TradeResult to registered TradeHandler
     * when TradeOrder get traded, publish StateChange
     *
     * @param tradeRequest a TradeRequest
     * @return a new TradeOrder from TradeRequest with no TradeResult and pending state
     * @throws TradeCantNotBeDoneException 当请求在要求的账户下无法完成时抛出
     */
    public Order applyTo(TradeRequest tradeRequest, Account account) throws TradeCantNotBeDoneException {
        // TODO 风险控制，已完成数量可行性控制
        Asset asset = account.asset();

        if (tradeRequest.tradeSide() == TradeSide.BUY) {
            if (tradeRequest.priceType() == PriceType.LIMITED) {
                LimitedPriceTradeRequest l = (LimitedPriceTradeRequest) tradeRequest;
                if (asset.cantLock(l.netValue())) {
                    throw new TradeCantNotBeDoneException("该账户下没有足够的可用资金满足买入请求");
                }
                Order order = makeOrderAndRun(tradeRequest, account);
                asset.lockCash(l.netValue(), order);
                return order;
            } else {
                // TODO 搞清楚市价单如何判定购买时要锁定的数量，目前暂时不锁定
                return makeOrderAndRun(tradeRequest, account);
            }
        } else {
            if (asset.cantLock(tradeRequest.securityCode(), tradeRequest.shares())) {
                throw new TradeCantNotBeDoneException("该账户下没有足够的股票满足卖出请求");
            }
            Order order = makeOrderAndRun(tradeRequest, account);
            asset.lockShares(tradeRequest.securityCode, tradeRequest.shares, order);
            return order;
        }
    }

    private Order makeOrderAndRun(TradeRequest tradeRequest, Account account) {
        Order order;
        String internalId = UUID.randomUUID().toString();
        order = new Order(account.id(), internalId, tradeRequest);
        order.startTrading();
        orderRepository.save(order);
        return order;
    }

}
