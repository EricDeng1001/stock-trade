package org.example.trade.adapter.translator;

import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;
import org.example.trade.interfaces.order.OrderDTO;

public class OrderTranslator implements Translator<Order, OrderDTO> {

    @Override
    public Order from(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO to(Order order) {
        TradeRequest requirement = order.requirement();
        return new OrderDTO(
            OrderIdTranslator.instance().to(order.id()),
            requirement.securityCode().toString(),
            requirement.shares().value().toString(),
            switch (requirement.priceType()) {
                case MARKET -> null;
                case LIMITED -> ((LimitedPriceTradeRequest) requirement).targetPrice().toString();
            }
        );
    }

}
