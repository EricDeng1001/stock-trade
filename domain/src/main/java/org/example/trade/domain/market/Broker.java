package org.example.trade.domain.market;

import engineering.ericdeng.architecture.domain.model.Identity;

import java.util.Objects;

public class Broker extends Identity {

    private final String id;

    public Broker(String id) {
        this.id = id.toLowerCase();
    }

    public static Broker valueOf(String x) {
        return new Broker(x);
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Broker broker = (Broker) o;
        return id.equals(broker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
