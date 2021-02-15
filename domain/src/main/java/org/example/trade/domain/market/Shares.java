package org.example.trade.domain.market;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

public class Shares implements Comparable<Shares> {

    public static final Shares ZERO = new Shares(0);

    private final BigInteger value;

    public Shares(BigInteger value) {
        this.value = value;
    }

    public Shares(long value) {
        this(BigInteger.valueOf(value));
    }

    public Shares add(Shares x) {
        return new Shares(value.add(x.value));
    }

    public Shares subtract(Shares x) {
        return new Shares(value.subtract(x.value));
    }

    public Shares multiply(BigDecimal x) {
        return new Shares(x.multiply(new BigDecimal(value)).toBigInteger());
    }

    public Shares multiplyExact(BigDecimal x) {
        return new Shares(x.multiply(new BigDecimal(value)).toBigIntegerExact());
    }

    public Shares divide(BigDecimal x) {
        return new Shares(x.divide(new BigDecimal(value), RoundingMode.CEILING).toBigInteger());
    }

    public Shares divideExact(BigDecimal x) {
        return new Shares(x.divide(new BigDecimal(value), RoundingMode.UNNECESSARY).toBigInteger());
    }

    public Money multiply(Price price) {
        return price.unitValue().multiply(value);
    }

    public BigInteger value() {
        return value;
    }

    public boolean equals(Shares o) {
        if (this == o) { return true; }
        if (o == null) { return false; }
        return value.equals(o.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Shares{" +
            value +
            '}';
    }

    @Override
    public int compareTo(@NotNull Shares o) {
        return value.compareTo(o.value);
    }

}
