package org.example.trade.infrastructure;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;

public class JavaMapAccountRepository extends JavaMapRepository<AccountId, Account> {

    @Override
    protected AccountId getId(Account r) {
        return r.id();
    }

}
