package org.example.trade.adapter.persistence;

import org.example.trade.adapter.persistence.model.account.Account;
import org.example.trade.adapter.persistence.translator.AccountTranslator;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JPAAccountRepository JPAAccountRepository;

    public AccountRepositoryImpl(JPAAccountRepository JPAAccountRepository) {
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
        JPAAccountRepository.save(AccountTranslator.from(account));
    }

    @Override
    public Collection<org.example.trade.domain.account.Account> findAll() {
        return JPAAccountRepository.findAll().stream().map(AccountTranslator::from).collect(Collectors.toList());
    }

}

