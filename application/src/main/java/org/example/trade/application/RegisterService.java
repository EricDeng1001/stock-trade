package org.example.trade.application;

import org.example.trade.adapter.broker.AccountNotSupportedException;
import org.example.trade.adapter.broker.SingleAccountBrokerService;
import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegisterService implements SingleAccountBrokerServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private final Map<AccountId, SingleAccountBrokerService> serviceMap;

    private final AccountRepository accountRepository;

    private final OrderQueueRepository orderQueueRepository;

    public RegisterService(AccountRepository accountRepository,
                           OrderQueueRepository orderQueueRepository) {
        this.accountRepository = accountRepository;
        this.orderQueueRepository = orderQueueRepository;
        this.serviceMap = new ConcurrentHashMap<>();
    }

    public void registerAccount(AccountId supportedAccount, SingleAccountBrokerService service) {
        Account account = new Account(supportedAccount, "");
        accountRepository.save(account);
        OrderQueue orderQueue = new OrderQueue(account.id());
        orderQueueRepository.add(orderQueue);
        serviceMap.put(supportedAccount, service);
        logger.info("{} 已经注册", account);
    }

    @Override
    public SingleAccountBrokerService getOrNew(AccountId accountId) throws AccountNotSupportedException {
        if (!serviceMap.containsKey(accountId)) { throw new AccountNotSupportedException(accountId); }
        return serviceMap.get(accountId);
    }

}
