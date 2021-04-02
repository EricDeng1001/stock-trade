package org.example.trade.adapter.broker;

import org.example.trade.application.RegisterService;
import org.example.trade.application.SyncService;
import org.example.trade.application.TradeService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;

public abstract class SingleAccountBrokerService {

    protected final RegisterService registerService;

    protected final SyncService syncService;

    protected final TradeService tradeService;

    protected final AccountId supportedAccount;

    protected SingleAccountBrokerService(AccountId supportedAccount,
                                         RegisterService registerService,
                                         SyncService syncService,
                                         TradeService tradeService) {
        this.supportedAccount = supportedAccount;
        this.syncService = syncService;
        this.tradeService = tradeService;
        this.registerService = registerService;
        registerService.registerAccount(supportedAccount);
    }

    public AccountId supportedAccount() {
        return supportedAccount;
    }

    public abstract boolean activate(String config);

    public abstract boolean deactivate();

    public abstract void queryAsset();

    public abstract void submit(Order order);

    public abstract void withdraw(OrderId order);

}
