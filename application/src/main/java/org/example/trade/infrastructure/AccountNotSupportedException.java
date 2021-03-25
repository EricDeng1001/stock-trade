package org.example.trade.infrastructure;

import org.example.trade.domain.account.AccountId;

public class AccountNotSupportedException extends IllegalArgumentException {

    private final AccountId account;

    public AccountNotSupportedException(AccountId account) {
        this.account = account;
    }

    public AccountId account() {
        return account;
    }

}
