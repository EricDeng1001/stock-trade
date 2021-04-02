package org.example.trade.persistence.translator;

import org.example.trade.domain.market.Broker;
import org.example.trade.persistence.model.account.Account;
import org.example.trade.persistence.model.account.AccountId;

public class AccountTranslator {

    public static org.example.trade.domain.account.Account from(Account account) {
        if (account == null) return null;
        return new org.example.trade.domain.account.Account(
            from(account.getId()),
            account.getConfig(),
            account.isActivated()
        );
    }

    public static org.example.trade.domain.account.AccountId from(AccountId idRecord) {
        return new org.example.trade.domain.account.AccountId(Broker.valueOf(idRecord.getBroker()),
                                                              idRecord.getAccountBrokerId());
    }

    public static Account from(org.example.trade.domain.account.Account account) {
        return new Account(
            account.id(),
            account.config(),
            account.isActivated()
        );
    }

    public static AccountId from(org.example.trade.domain.account.AccountId id) {
        AccountId idRecord = new AccountId();
        idRecord.setBroker(id.broker().id());
        idRecord.setAccountBrokerId(id.brokerId());
        return idRecord;
    }

}
