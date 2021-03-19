package org.example.trade.domain.market;

import engineering.ericdeng.architecture.domain.model.Identity;

public class Broker extends Identity {

    private final String id;

    public Broker(String id) {
        this.id = id;
    }

    public static Broker valueOf(String x) {
        return new Broker(x);
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return "Broker{" +
            id +
            '}';
    }

}
