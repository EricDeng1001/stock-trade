package org.example.trade.adapter.jpa;

import org.example.trade.adapter.jpa.model.account.Account;
import org.example.trade.adapter.jpa.model.account.AccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, AccountId> {
}
