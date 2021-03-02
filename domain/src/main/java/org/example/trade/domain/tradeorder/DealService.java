package org.example.trade.domain.tradeorder;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.account.Asset;

import java.time.Instant;

public class DealService {

    protected final AccountRepository accountRepository;

    public DealService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void makeDeal(TradeOrder tradeOrder, Deal deal, Instant time) {
        Account account = accountRepository.findById(tradeOrder.account());
        Asset asset = account.asset();
        if (tradeOrder.tradeRequest().tradeSide() == TradeSide.BUY) {
            Asset.CashResource cashResource = account.getCashLock(tradeOrder);
            cashResource.consume(deal.value());
            asset.gainShares(tradeOrder.tradeRequest().securityCode(), deal.shares());
        } else {
            Asset.SharesResource sharesResource = account.getSharesLock(tradeOrder);
            sharesResource.consume(deal.shares());
            asset.gainCash(deal.value());
        }
        tradeOrder.makeDeal(deal, time);
    }

    public void finishOrder(TradeOrder tradeOrder) {
        Account account = accountRepository.findById(tradeOrder.account());
        if (tradeOrder.tradeRequest().tradeSide() == TradeSide.BUY) {
            Asset.CashResource cashResource = account.getCashLock(tradeOrder);
            cashResource.clear();
        } else {
            Asset.SharesResource sharesResource = account.getSharesLock(tradeOrder);
            sharesResource.clear();
        }
        tradeOrder.finish();
    }

}
