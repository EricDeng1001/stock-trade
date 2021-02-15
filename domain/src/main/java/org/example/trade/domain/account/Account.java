package org.example.trade.domain.account;

import org.example.trade.domain.market.Broker;

public class Account {

    private final Id id;

    private Asset asset;

    public Account(Broker broker, String brokerId) {
        this.id = new Id(broker, brokerId);
    }

    public Id id() {
        return id;
    }

    public Broker broker() {
        return id.broker;
    }

    public static class Id {

        private final Broker broker;

        private final String brokerId;

        public Id(Broker broker, String brokerId) {
            this.broker = broker;
            this.brokerId = brokerId;
        }

        public Broker broker() {
            return broker;
        }

        public String brokerId() {
            return brokerId;
        }

    }

}
