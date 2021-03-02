package org.example.trade.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

public interface DealService {

    void makeDeal(String broker, String orderId, BigInteger shares, BigDecimal price, Instant time);

    void finishOrder(String broker, String orderId);

}
