package org.example.trade.adapter.rest.translator;

import org.example.trade.domain.order.OrderId;

import java.time.LocalDate;
import java.util.Base64;

public class OrderIdTranslator {

    private static final String delimiter = "//";

    private OrderIdTranslator() {}

    public static OrderId from(String orderId) {
        try {
            String[] x = new String(Base64.getDecoder().decode(orderId)).split(delimiter);
            return new OrderId(
                AccountIdTranslator.from(x[1]),
                LocalDate.parse(x[0]),
                Integer.parseInt(x[2])
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException("所给账户不是合法的订单号");
        }
    }

    public static String from(OrderId id) {
        return Base64.getEncoder().encodeToString((id.tradeDay()
            + delimiter
            + AccountIdTranslator.from(id.accountId())
            + delimiter
            + id.uid()).getBytes());
    }

}
