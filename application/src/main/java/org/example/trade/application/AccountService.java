package org.example.trade.application;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.infrastructure.SingleAccountBrokerService;
import org.example.trade.infrastructure.SingleAccountBrokerServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    private final SingleAccountBrokerServiceFactory factory;

    private final AssetService assetService;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          SingleAccountBrokerServiceFactory factory,
                          AssetService assetService) {
        this.accountRepository = accountRepository;
        this.factory = factory;
        this.assetService = assetService;
    }

    public boolean activateAccount(AccountId accountId, String config) {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new NoSuchElementException("所指定的账户不存在");
        }
        SingleAccountBrokerService service = factory.getOrNew(accountId);
        account.changeConfig(config);
        if (service.activate(config)) {
            if (!account.isActivated()) {
                assetService.syncAssetFromBroker(accountId);
                account.activate();
            }
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    public boolean deactivate(AccountId accountId) {
        SingleAccountBrokerService service = factory.getOrNew(accountId);
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new NoSuchElementException("所指定的账户不存在");
        }
        if (!account.isActivated()) { throw new IllegalArgumentException("所选账户未激活"); }
        if (service.deactivate()) {
            account.deactivate();
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    public Collection<Account> getAll() {
        return accountRepository.findAll();
    }

}
