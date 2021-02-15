package org.example.trade.domain.market;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SharesTest {

    private static final Shares P11 = new Shares(11);

    private static final Shares P5 = new Shares(5);

    private static final Shares P10 = new Shares(10);

    private static final Shares P15 = new Shares(15);

    private static final Shares N3 = new Shares(-3);

    private static final Shares P7 = new Shares(7);

    private static final Shares N10 = new Shares(-10);

    private static final Shares P0 = new Shares(0);

    private static final Shares IP0 = P0;

    private static final Shares IN0 = new Shares(-0);

    private static final Shares BP0 = new Shares(BigInteger.ZERO);

    private static final Shares BN0 = new Shares(BigInteger.ZERO.negate());

    private static final Shares IP10 = new Shares(10);

    private static final Shares IN10 = new Shares(-10);

    private static final Shares BP10 = new Shares(BigInteger.TEN);

    private static final Shares BN10 = new Shares(BigInteger.TEN.negate());

    private static final Shares P50 = new Shares(BigInteger.valueOf(50));

    private static final Shares P9 = new Shares(9);

    private static final BigDecimal P1o1 = new BigDecimal("1.1");

    private static final BigDecimal P10o0 = new BigDecimal("10");

    @Test
    @DisplayName("10 add 5 should return 15")
    void addTest1() {
        assertEquals(P15, P10.add(P5));
    }

    @Test
    @DisplayName("10 add -3 should return 7")
    void addTest2() {
        assertEquals(P7, P10.add(N3));
    }

    @Test
    @DisplayName("-10 add 10 should return 0")
    void addTest3() {
        assertEquals(P0, P10.add(N10));
    }

    @Test
    @DisplayName("0 should equals -0, no matter how its constructed")
    void equalsTest1() {
        assertEquals(IP0, IN0);
        assertEquals(BP0, BN0);
        assertEquals(IP0, BP0);
    }

    @Test
    @DisplayName("construct by integer should be equal to construct by BigInteger")
    void equalsTest2() {
        assertEquals(IP10, BP10);
        assertEquals(BN10, IN10);
    }

    @Test
    @DisplayName("10 * 5 should equals 50")
    void multiplyTest1() {

        assertEquals(P50, P5.multiply(P10o0));
    }

    @Test
    @DisplayName("9 * 1.1 should equals 9")
    void multiplyTest2() {
        assertEquals(P9, P9.multiply(P1o1));
    }

    @Test
    @DisplayName("9 * 1.1 exact should throws ArithmeticException")
    void multiplyExactTest1() {
        assertThrows(ArithmeticException.class, () -> P9.multiplyExact(P1o1));
    }

    @Test
    @DisplayName("10 * 1.1 exact should equals 11")
    void multiplyExactTest2() {
        assertEquals(P11, P10.multiplyExact(P1o1));
    }

}
