package org.example.trade.adapter.broker.mock;

import org.example.trade.adapter.broker.AccountNotSupportedException;
import org.example.trade.adapter.broker.SingleAccountBrokerService;
import org.example.trade.adapter.broker.SingleAccountBrokerServiceFactory;
import org.example.trade.domain.account.AccountId;
import org.springframework.stereotype.Component;

@Component
public class SingleAccountFactory implements SingleAccountBrokerServiceFactory {

    private final MockSingleAccountBrokerService singleAccountBrokerService;

    public SingleAccountFactory(MockSingleAccountBrokerService singleAccountBrokerService) {
        this.singleAccountBrokerService = singleAccountBrokerService;
    }

    @Override
    public SingleAccountBrokerService getOrNew(AccountId accountId) throws AccountNotSupportedException {
        if (!singleAccountBrokerService.supportedAccount().equals(accountId)) {
            throw new AccountNotSupportedException(accountId);
        }
        return singleAccountBrokerService;
    }

}
