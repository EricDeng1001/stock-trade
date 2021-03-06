package org.example.trade.application;

import org.example.trade.domain.account.AccountId;
import org.example.trade.port.broker.AccountNotSupportedException;
import org.example.trade.port.broker.SingleAccountBrokerService;

public interface SingleAccountBrokerServiceFactory {

    /**
     *
     * @param accountId 要处理的账号
     * @return 可以处理该账户的服务实例，可能是本地对象，也可能是远程对象
     * @throws AccountNotSupportedException 如果没有可以处理该账户的对象，抛出一个错误
     */
    SingleAccountBrokerService getOrNew(AccountId accountId) throws AccountNotSupportedException;
}
