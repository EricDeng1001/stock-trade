package org.example.trade.domain.account;

public interface AccountRepository {

    Account findById(AccountId id);

    void save(Account account);
}
