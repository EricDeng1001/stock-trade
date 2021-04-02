package org.example.trade.domain.market;

import java.util.Objects;

public class SecurityCode {

    // TODO regex check
    private final String value;

    private SecurityCode(String value) {
        this.value = value;
    }

    public static SecurityCode valueOf(String s) {
        return new SecurityCode(s);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "SecurityCode{" +
            value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        SecurityCode that = (SecurityCode) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    // TODO
    public Market market() {
        return null;
    }

}
