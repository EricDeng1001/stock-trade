package org.example.trade.domain.tradeorder;

import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.account.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class TradeService extends DomainEventSubscriber<TradeEvent> {

    protected final TradeOrderRepository tradeOrderRepository;

    protected final AccountRepository accountRepository;

    public TradeService(TradeOrderRepository tradeOrderRepository,
                        AccountRepository accountRepository) {
        this.tradeOrderRepository = tradeOrderRepository;
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
    public TradeOrder applyTo(TradeRequest tradeRequest, Account account) throws TradeCantNotBeDoneException {
        // TODO 风险控制，已完成数量可行性控制
        Asset asset = account.asset();

        if (tradeRequest.tradeSide() == TradeSide.BUY) {
            if (tradeRequest.priceType() == PriceType.LIMITED) {
                LimitedPriceTradeRequest l = (LimitedPriceTradeRequest) tradeRequest;
                if (asset.cantLock(l.netValue())) {
                    throw new TradeCantNotBeDoneException("该账户下没有足够的可用资金满足买入请求");
                }
                TradeOrder order = makeOrderAndRun(tradeRequest, account);
                asset.lockCash(l.netValue(), order);
                return order;
            } else {
                // TODO 搞清楚市价单如何判定购买时要锁定的数量，目前暂时不锁定
                return makeOrderAndRun(tradeRequest, account);
            }
        } else {
            if (asset.cantLock(tradeRequest.stockCode, tradeRequest.shares)) {
                throw new TradeCantNotBeDoneException("该账户下没有足够的股票满足卖出请求");
            }
            TradeOrder order = makeOrderAndRun(tradeRequest, account);
            asset.lockShares(tradeRequest.stockCode, tradeRequest.shares, order);
            return order;
        }
    }

    @Override
    public void handle(TradeEvent tradeEvent) {
        TradeOrder.Id orderId = tradeEvent.orderId();
        TradeOrder tradeOrder = tradeOrderRepository.findById(orderId);
        Account account = accountRepository.findById(tradeOrder.account());
        if (tradeEvent instanceof OrderTraded) {
            if (tradeOrder.tradeRequest().tradeSide() == TradeSide.BUY) {
                Asset.CashLock cashLock = account.getCashLock(tradeOrder);
                cashLock.consume(((OrderTraded) tradeEvent).deal().value());
            } else {
                Asset.SharesLock sharesLock = account.getSharesLock(tradeOrder);
                sharesLock.consume(((OrderTraded) tradeEvent).deal().shares());
            }
        } else if (tradeEvent instanceof OrderFinished) {
            if (tradeOrder.tradeRequest().tradeSide() == TradeSide.BUY) {
                Asset.CashLock cashLock = account.getCashLock(tradeOrder);
                cashLock.dispose();
            } else {
                Asset.SharesLock sharesLock = account.getSharesLock(tradeOrder);
                sharesLock.dispose();
            }
        }
    }

    /**
     * 开始交易订单
     *
     * @param order 要交易的订单
     */
    protected abstract void startTrade(TradeOrder order);

    @NotNull
    private TradeOrder makeOrderAndRun(TradeRequest tradeRequest, Account account) {
        TradeOrder order;
        String internalId = UUID.randomUUID().toString();
        order = new TradeOrder(account.id(), internalId, tradeRequest);
        startTrade(order);
        order.startTrading();
        tradeOrderRepository.save(order);
        return order;
    }

}
