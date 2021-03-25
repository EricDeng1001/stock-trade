package org.example.trade.adapter.persistence;

import org.example.trade.adapter.persistence.model.account.Account;
import org.example.trade.adapter.persistence.model.account.AccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAAccountRepository extends JpaRepository<Account, AccountId> {
}
