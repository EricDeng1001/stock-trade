package org.example.trade.domain.account;

public interface AccountRepository {

    Account findById(Account.Id id);

}
