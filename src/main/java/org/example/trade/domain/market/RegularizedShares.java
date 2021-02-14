package org.example.trade.domain.market;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class RegularizedShares extends Shares {

    public static final RegularizedShares ZERO = new RegularizedShares(Shares.ZERO);

    private static final BigInteger V100 = BigInteger.TEN.multiply(BigInteger.TEN);

    public static final RegularizedShares RS100 = new RegularizedShares(V100);

    public RegularizedShares(BigInteger value) {
        super(value.divide(V100).multiply(V100));
    }

    private RegularizedShares(Shares value) {
        super(value.value());
    }

    public RegularizedShares(long value) {
        super((value / 100) * 100);
    }

    public RegularizedShares add(RegularizedShares x) {
        return new RegularizedShares(super.add(x));
    }

    public RegularizedShares subtract(RegularizedShares x) {
        return new RegularizedShares(super.subtract(x));
    }

    public RegularizedShares multiply(BigDecimal x) {
        return new RegularizedShares(x.multiply(new BigDecimal(value())).toBigInteger());
    }

    public RegularizedShares multiplyExact(BigDecimal x) {
        return new RegularizedShares(x.multiply(new BigDecimal(value())).toBigIntegerExact());
    }

    public RegularizedShares divide(BigDecimal x) {
        return new RegularizedShares(x.divide(new BigDecimal(value()), RoundingMode.CEILING).toBigInteger());
    }

    public RegularizedShares divideExact(BigDecimal x) {
        return new RegularizedShares(x.divide(new BigDecimal(value()), RoundingMode.UNNECESSARY).toBigInteger());
    }

}
