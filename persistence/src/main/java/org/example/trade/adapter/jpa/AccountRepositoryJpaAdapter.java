package org.example.trade.adapter.jpa;

import org.example.trade.adapter.jpa.model.account.Account;
import org.example.trade.adapter.jpa.translator.AccountTranslator;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AccountRepositoryJpaAdapter implements AccountRepository {

    private final JpaAccountRepository JPAAccountRepository;

    public AccountRepositoryJpaAdapter(JpaAccountRepository JPAAccountRepository) {
        this.JPAAccountRepository = JPAAccountRepository;
    }

    @Override
    public org.example.trade.domain.account.Account findById(AccountId id) {
        Optional<Account> accountRecord = JPAAccountRepository.findById(
            AccountTranslator.from(id));
        if (accountRecord.isEmpty()) { return null; }
        return AccountTranslator.from(accountRecord.get());
    }

    @Override
    public void save(org.example.trade.domain.account.Account account) {
        JPAAccountRepository.saveAndFlush(AccountTranslator.from(account));
    }

    @Override
    public Collection<org.example.trade.domain.account.Account> findAll() {
        return JPAAccountRepository.findAll().stream().map(AccountTranslator::from).collect(Collectors.toList());
    }

}

