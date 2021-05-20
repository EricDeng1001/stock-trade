package org.example.trade.port.broker;

import org.example.trade.application.TradeService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.interfaces.SyncService;

public abstract class SingleAccountBrokerService {

    protected final SyncService syncService;

    protected final TradeService tradeService;

    protected final AccountId supportedAccount;

    protected SingleAccountBrokerService(AccountId supportedAccount,
                                         SyncService syncService,
                                         TradeService tradeService) {
        this.supportedAccount = supportedAccount;
        this.syncService = syncService;
        this.tradeService = tradeService;
    }

    public AccountId supportedAccount() {
        return supportedAccount;
    }

    public abstract boolean connect(String config);

    public abstract boolean disconnect();

    public abstract void queryAsset();

    public abstract void submit(Order order);

    public abstract void withdraw(OrderId order);

}
