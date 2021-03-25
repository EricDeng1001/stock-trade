package org.example.trade.domain.account;

import java.util.Collection;
import java.util.NoSuchElementException;

public interface AccountRepository {

    Account findById(AccountId id) throws NoSuchElementException;

    void save(Account account);

    Collection<Account> findAll();

}
