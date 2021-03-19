package org.example.trade.adapter.translator;

import org.example.trade.domain.account.AccountId;

public class AccountIdTranslator implements Translator<AccountId, String> {

    private static final AccountIdTranslator instance = new AccountIdTranslator();

    public static AccountIdTranslator instance() {
        return instance;
    }

    @Override
    public AccountId from(String s) {
        return null;
    }

    @Override
    public String to(AccountId accountId) {
        return null;
    }

}
