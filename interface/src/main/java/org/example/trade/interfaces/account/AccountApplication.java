package org.example.trade.interfaces.account;

import java.util.List;

public interface AccountApplication {

    boolean activateAccount(ActivateAccountCommand command);

    List<String> querySupportedAccounts();

    void changeConfig(String accountId, String newConfig);

}
