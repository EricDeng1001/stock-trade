package org.example.trade.domain.account;

import org.example.trade.domain.market.Broker;

import java.time.Instant;

public class Account {

    private final Id id;

    private final Instant createdAt;

    private String name;

    private Asset asset;

    public Account(Broker.Id broker, String brokerId, String name) {
        this.id = new Id(broker, brokerId);
        this.name = name;
        this.createdAt = Instant.now();
    }

    public Account(Broker.Id broker, String brokerId, String name, Instant createdAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.id = new Id(broker, brokerId);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public String name() {
        return name;
    }

    public void changeNameTo(String newName) {
        name = newName;
    }

    public Id id() {
        return id;
    }

    public static class Id {

        private final Broker.Id broker;

        private final String brokerId;

        public Id(Broker.Id broker, String brokerId) {
            this.broker = broker;
            this.brokerId = brokerId;
        }

        public Broker.Id broker() {
            return broker;
        }

        public String brokerId() {
            return brokerId;
        }

    }

}
