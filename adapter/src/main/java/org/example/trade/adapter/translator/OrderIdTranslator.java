package org.example.trade.adapter.translator;

import org.example.trade.domain.order.OrderId;

import java.time.LocalDate;

public class OrderIdTranslator implements Translator<OrderId, String> {

    private static final OrderIdTranslator instance = new OrderIdTranslator();

    private static final String delimiter = "/";

    public static OrderIdTranslator instance() {
        return instance;
    }

    @Override
    public OrderId from(String orderId) {
        String[] x = orderId.split(delimiter);
        if (x.length != 3) { throw new IllegalArgumentException(); }
        return new OrderId(
            AccountIdTranslator.instance().from(x[1]),
            LocalDate.parse(x[0]),
            Integer.parseInt(x[2])
        );
    }

    @Override
    public String to(OrderId id) {
        return (id.tradeDay()
            + delimiter
            + id.accountId()
            + delimiter
            + id.uid());
    }

}
