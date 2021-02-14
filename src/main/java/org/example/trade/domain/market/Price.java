package org.example.trade.domain.market;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price {

    /**
     * price is an unit of money
     */
    private final Money unit;

    public Price(Money money) {
        this.unit = money;
    }

    public Price(BigDecimal value) {
        this.unit = new Money(value);
    }

    public Price(String value) {
        this.unit = new Money(value);
    }

    public Price(long value) {
        this.unit = new Money(value);
    }

    public Price(double unit) {
        this.unit = new Money(unit);
    }

    public Price add(Price x) {
        return new Price(unit.add(x.unit));
    }

    public Price subtract(Price y) {
        return new Price(unit.subtract(y.unit));
    }

    public Price multiply(BigDecimal z) {
        return new Price(unit.multiply(z));
    }

    public Price divide(BigDecimal a, RoundingMode roundingMode) {
        return new Price(unit.divide(a, roundingMode));
    }

    public Money multiply(Shares shares) {
        return unit.multiply(shares.value());
    }

    public Money unitValue() {
        return unit;
    }

    @Override
    public String toString() {
        return "Price{" +
            unit +
            '}';
    }

}
