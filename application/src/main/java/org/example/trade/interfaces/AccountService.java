package org.example.trade.interfaces;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface AccountService {

    @Transactional
    boolean activateAccount(AccountId accountId, String config);

    @Transactional
    void changeConfig(AccountId accountId, String config);

    Collection<Account> getAll();

}
