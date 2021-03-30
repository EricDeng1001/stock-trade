package org.example.trade.infrastructure.broker;

import org.example.trade.application.RegisterService;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.asset.AssetInfo;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;

public abstract class SingleAccountBrokerService {

    protected final AccountId supportedAccount;

    protected SingleAccountBrokerService(AccountId supportedAccount, RegisterService registerService) {
        this.supportedAccount = supportedAccount;
        registerService.registerAccount(supportedAccount);
    }

    public abstract boolean activate(String config);

    public abstract boolean deactivate();

    public AccountId supportedAccount() {
        return supportedAccount;
    }

    public abstract AssetInfo queryAsset();

    public abstract void submit(Order order);

    public abstract void withdraw(OrderId order);

}
