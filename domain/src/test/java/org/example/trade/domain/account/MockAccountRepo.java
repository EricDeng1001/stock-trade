package org.example.trade.domain.account;

import org.example.trade.domain.MockRepo;

public class MockAccountRepo extends MockRepo<AccountId, Account> {

    @Override
    protected AccountId getId(Account r) {
        return r.id();
    }

}
