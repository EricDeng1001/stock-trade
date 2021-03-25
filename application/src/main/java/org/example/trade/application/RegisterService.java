package org.example.trade.application;

import org.example.trade.domain.account.Account;
import org.example.trade.domain.account.AccountId;
import org.example.trade.domain.account.AccountRepository;
import org.example.trade.domain.queue.OrderQueue;
import org.example.trade.domain.queue.OrderQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation= Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    private final AccountRepository accountRepository;

    private final OrderQueueRepository orderQueueRepository;

    public RegisterService(AccountRepository accountRepository,
                           OrderQueueRepository orderQueueRepository) {this.accountRepository = accountRepository;
        this.orderQueueRepository = orderQueueRepository;
    }

    public void registerAccount(AccountId supportedAccount) {
        Account account = new Account(supportedAccount, "");
        accountRepository.save(account);
        OrderQueue orderQueue = new OrderQueue(account.id());
        orderQueueRepository.add(orderQueue);
        log.info("{} 已经注册", account);
        // 向service register注册自己
    }

}
