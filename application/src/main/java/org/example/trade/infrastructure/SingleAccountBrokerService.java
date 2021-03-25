package org.example.trade.infrastructure;

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

    public void submit(Order order) {
        submitImpl(order);
    }

    public void withdraw(OrderId order) {
        withdrawImpl(order);
    }

    public AssetInfo queryAsset() {
        return queryAssetImpl();
    }

    public AccountId supportedAccount() {
        return supportedAccount;
    }

    protected abstract AssetInfo queryAssetImpl();

    protected abstract void submitImpl(Order order);

    protected abstract void withdrawImpl(OrderId order);

}
