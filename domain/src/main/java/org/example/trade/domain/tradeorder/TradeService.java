package org.example.trade.domain.tradeorder;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.account.Asset;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public abstract class TradeService {

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
            if (asset.cantLock(tradeRequest.securityCode, tradeRequest.shares)) {
                throw new TradeCantNotBeDoneException("该账户下没有足够的股票满足卖出请求");
            }
            TradeOrder order = makeOrderAndRun(tradeRequest, account);
            asset.lockShares(tradeRequest.securityCode, tradeRequest.shares, order);
            return order;
        }
    }

    public void makeDeal(TradeOrder tradeOrder, Deal deal, Instant time) {
        Account account = accountRepository.findById(tradeOrder.account());
        if (tradeOrder.tradeRequest().tradeSide() == TradeSide.BUY) {
            Asset.CashLock cashLock = account.getCashLock(tradeOrder);
            cashLock.consume(deal.value());
        } else {
            Asset.SharesLock sharesLock = account.getSharesLock(tradeOrder);
            sharesLock.consume(deal.shares());
        }
        tradeOrder.makeDeal(deal, time);
    }

    public void finishOrder(TradeOrder tradeOrder) {
        Account account = accountRepository.findById(tradeOrder.account());
        if (tradeOrder.tradeRequest().tradeSide() == TradeSide.BUY) {
            Asset.CashLock cashLock = account.getCashLock(tradeOrder);
            cashLock.dispose();
        } else {
            Asset.SharesLock sharesLock = account.getSharesLock(tradeOrder);
            sharesLock.dispose();
        }
        tradeOrder.finish();
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
