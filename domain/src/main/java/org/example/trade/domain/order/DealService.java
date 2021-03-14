package org.example.trade.domain.order;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.account.Asset;

import java.time.Instant;

public class DealService {

    protected final AccountRepository accountRepository;

    public DealService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void makeDeal(Order order, Deal deal, Instant time) {
        Account account = accountRepository.findById(order.account());
        Asset asset = account.asset();
        if (order.tradeRequest().tradeSide() == TradeSide.BUY) {
            Asset.CashResource cashResource = account.getCashLock(order);
            cashResource.consume(deal.value());
            asset.gainShares(order.tradeRequest().securityCode(), deal.shares());
        } else {
            Asset.SharesResource sharesResource = account.getSharesLock(order);
            sharesResource.consume(deal.shares());
            asset.gainCash(deal.value());
        }
        order.makeDeal(deal, time);
    }

    public void finishOrder(Order order) {
        Account account = accountRepository.findById(order.account());
        if (order.tradeRequest().tradeSide() == TradeSide.BUY) {
            Asset.CashResource cashResource = account.getCashLock(order);
            cashResource.clear();
        } else {
            Asset.SharesResource sharesResource = account.getSharesLock(order);
            sharesResource.clear();
        }
        order.finish(time);
    }

}
