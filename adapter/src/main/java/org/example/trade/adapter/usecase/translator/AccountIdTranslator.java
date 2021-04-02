package org.example.trade.adapter.usecase.translator;

import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.market.Broker;

public class AccountIdTranslator {

    private static final String delimiter = "@";

    private AccountIdTranslator() {}

    public static AccountId from(String s) {
        try {
            String[] x = s.split(delimiter);
            return new AccountId(Broker.valueOf(x[1]), x[0]);
        } catch (Exception exception) {
            throw new IllegalArgumentException("所给参数不是合法的账户号: " + s);
        }
    }

    public static String from(AccountId accountId) {
        return accountId.brokerId() + delimiter + accountId.broker().id();
    }

}
