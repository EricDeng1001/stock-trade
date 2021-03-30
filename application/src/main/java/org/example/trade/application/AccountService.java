package org.example.trade.application;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.example.trade.infrastructure.broker.SingleAccountBrokerServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final OrderQueueRepository orderQueueRepository;

    private final AccountRepository accountRepository;

    private final SingleAccountBrokerServiceFactory factory;

    private final AssetService assetService;

    @Autowired
    public AccountService(OrderQueueRepository orderQueueRepository,
                          AccountRepository accountRepository,
                          SingleAccountBrokerServiceFactory factory,
                          AssetService assetService) {
        this.orderQueueRepository = orderQueueRepository;
        this.accountRepository = accountRepository;
        this.factory = factory;
        this.assetService = assetService;
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    public void changeConfig(AccountId accountId, String config) {
        Account account = accountRepository.findById(accountId);
        account.changeConfig(config);
        accountRepository.save(account);
    }

    public void registerAccount(AccountId supportedAccount) {
        Account account = new Account(supportedAccount, "");
        accountRepository.save(account);
        OrderQueue orderQueue = new OrderQueue(account.id());
        orderQueueRepository.add(orderQueue);
        logger.info("{} 已经注册", account);
        // 向service register注册自己
    }

    public Collection<Account> getAll() {
        return accountRepository.findAll();
    }

}
