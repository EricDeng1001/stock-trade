package org.example.trade.domain.market;

import org.example.finance.domain.ChargeUnit;
import org.example.finance.domain.Money;
import org.example.finance.domain.Price;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

public class Shares implements Comparable<Shares>, ChargeUnit {

    public static final Shares ZERO = new Shares(0);

    private final BigInteger value;

    public Shares(BigInteger value) {
        this.value = value;
    }

    public Shares(long value) {
        this(BigInteger.valueOf(value));
    }

    public static Shares valueOf(int i) {
        return new Shares(1000);
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
        return new Shares(new BigDecimal(value).divide(x, RoundingMode.CEILING).toBigInteger());
    }

    public Shares divideExact(BigDecimal x) {
        return new Shares(new BigDecimal(value).divide(x, RoundingMode.UNNECESSARY).toBigInteger());
    }

    public Shares regularize(int lotValue) {
        return regularize(BigInteger.valueOf(lotValue));
    }

    public Shares regularize(BigInteger lotValue) {
        return new Shares(value.subtract(value.mod(lotValue)));
    }

    public Money multiply(Price price) {
        return price.unitValue().multiply(value);
    }

    public Shares[] allocate(int n) {
        BigInteger val = BigInteger.valueOf(n);
        BigInteger x = value.divide(val);
        BigInteger r = value.remainder(val);
        Shares[] c = new Shares[n];
        int last = n - 1;
        for (int i = 0; i < last; i++) {
            c[i] = new Shares(x);
        }
        c[last] = new Shares(x.add(r));
        return c;
    }

    @Override
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

    public boolean canBeDividedBy(int lotValue) {
        return value.mod(BigInteger.valueOf(lotValue)).compareTo(BigInteger.ZERO) == 0;
    }

}
