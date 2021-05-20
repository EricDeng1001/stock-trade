package org.example.trade.application;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.example.trade.port.broker.AccountNotSupportedException;
import org.example.trade.port.broker.SingleAccountBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegisterService implements SingleAccountBrokerServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private final Map<AccountId, SingleAccountBrokerService> serviceMap;

    private final AccountRepository accountRepository;

    private final OrderQueueRepository orderQueueRepository;

    @Autowired
    public RegisterService(
        AccountRepository accountRepository,
        OrderQueueRepository orderQueueRepository,
        List<SingleAccountBrokerService> services
    ) {
        this.accountRepository = accountRepository;
        this.orderQueueRepository = orderQueueRepository;
        this.serviceMap = new ConcurrentHashMap<>();
        for (SingleAccountBrokerService service : services) {
            this.registerAccount(service);
        }
    }

    private void registerAccount(SingleAccountBrokerService service) {
        AccountId supportedAccount = service.supportedAccount();
        Account account = new Account(supportedAccount, "");
        accountRepository.save(account);
        OrderQueue orderQueue = new OrderQueue(account.id());
        orderQueueRepository.add(orderQueue);
        serviceMap.put(supportedAccount, service);
        logger.info("{} 已经注册", supportedAccount);
    }

    @Override
    public SingleAccountBrokerService getOrNew(AccountId accountId) throws AccountNotSupportedException {
        if (!serviceMap.containsKey(accountId)) { throw new AccountNotSupportedException(accountId); }
        return serviceMap.get(accountId);
    }

}
