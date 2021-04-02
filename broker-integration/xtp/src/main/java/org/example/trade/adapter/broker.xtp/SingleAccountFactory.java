package org.example.trade.adapter.broker.xtp;

import org.example.trade.adapter.broker.AccountNotSupportedException;
import org.example.trade.adapter.broker.SingleAccountBrokerService;
import org.example.trade.adapter.broker.SingleAccountBrokerServiceFactory;
import org.example.trade.domain.account.AccountId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("xtp")
@Component
public class SingleAccountFactory implements SingleAccountBrokerServiceFactory {

    private final XTPSingleAccountSingleServiceAdapter singleAccountBrokerService;

    public SingleAccountFactory(
        XTPSingleAccountSingleServiceAdapter singleAccountBrokerService) {
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