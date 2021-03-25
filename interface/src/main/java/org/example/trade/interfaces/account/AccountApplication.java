package org.example.trade.interfaces.account;

import org.example.trade.interfaces.order.OrderDTO;

import java.util.List;

public interface AccountApplication {

    boolean activateAccount(ActivateAccountCommand command);

    AccountDTO queryAccount(String accountId);

    List<String> queryAccounts();

    AccountDTO changeConfig(String accountId, String newConfig);

}
