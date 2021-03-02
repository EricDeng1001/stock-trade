package org.example.trade.domain.trade;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountRepository;

import java.util.HashMap;
import java.util.Map;

public class AR implements AccountRepository {

    private final Map<Account.Id, Account> map = new HashMap<>();

    @Override
    public Account findById(Account.Id id) {
        return map.get(id);
    }

    @Override
    public void save(Account account) {
        map.put(account.id(), account);
    }

}
