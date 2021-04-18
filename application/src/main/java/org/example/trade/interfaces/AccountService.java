package org.example.trade.interfaces;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;

import java.util.Collection;

public interface AccountService {

    boolean activateAccount(AccountId accountId, String config);

    void changeConfig(AccountId accountId, String config);

    Collection<Account> getAll();

}
