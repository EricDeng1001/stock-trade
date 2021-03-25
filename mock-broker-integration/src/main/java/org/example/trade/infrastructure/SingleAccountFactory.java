package org.example.trade.infrastructure;

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
