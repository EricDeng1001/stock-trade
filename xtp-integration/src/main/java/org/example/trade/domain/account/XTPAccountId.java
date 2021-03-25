package org.example.trade.domain.account;

import org.example.trade.domain.market.Broker;

public class XTPAccountId extends AccountId {

    public XTPAccountId(String brokerId) {
        super(Broker.valueOf("xtp"), brokerId);
    }

}
