package org.example.trade.adapter.infrastructure;

import org.example.trade.domain.account.AccountId;
import org.example.trade.infrastructure.broker.AccountNotSupportedException;
import org.example.trade.infrastructure.broker.SingleAccountBrokerService;
import org.example.trade.infrastructure.broker.SingleAccountBrokerServiceFactory;
import org.springframework.stereotype.Component;

@Component
public class SingleAccountFactory implements SingleAccountBrokerServiceFactory {

    private final XTPSingleAccountSingleServiceAdapter singleAccountBrokerService;

    public SingleAccountFactory(XTPSingleAccountSingleServiceAdapter singleAccountBrokerService) {
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