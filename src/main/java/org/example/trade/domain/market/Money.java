package org.example.trade.domain.market;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Money implements Comparable<Money> {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal v;

    public Money(BigDecimal v) {
        this.v = v.setScale(2, RoundingMode.CEILING);
    }

    public Money(String v) {
        this(new BigDecimal(v));
    }

    public Money(long v) {
        this(BigDecimal.valueOf(v));
    }

    public Money(double v) {
        this.v = BigDecimal.valueOf(v);
    }

    public Money add(Money x) {
        return new Money(v.add(x.v));
    }

    public Money subtract(Money y) {
        return new Money(v.subtract(y.v));
    }

    public Money multiply(BigDecimal z) {
        return new Money(v.multiply(z));
    }

    public Money multiply(BigInteger z) {
        return multiply(new BigDecimal(z));
    }

    public Money divide(BigDecimal a, RoundingMode roundingMode) {
        return new Money(v.divide(a, roundingMode));
    }

    public BigDecimal value() {
        return v;
    }

    public int compareTo(Money o) {
        return v.compareTo(o.v);
    }

    @Override
    public String toString() {
        return "Money{" +
            "v=" + v +
            '}';
    }

}
