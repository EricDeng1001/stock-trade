package org.example.trade.adapter.rest.translator;

import org.example.trade.adapter.rest.boundary.OrderDTO;
import org.example.trade.adapter.rest.boundary.TradeDTO;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.Trade;
import org.example.trade.domain.order.TradeSide;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;

import java.util.stream.Collectors;

public class OrderTranslator {

    private OrderTranslator() {}

    public static OrderDTO from(Order order) {
        if (order == null) { return null; }
        TradeRequest requirement = order.requirement();
        return new OrderDTO(
            OrderIdTranslator.from(order.id()),
            requirement.securityCode().value(),
            requirement.tradeSide() == TradeSide.BUY ?
                requirement.shares().value().toString()
                : "-" + requirement.shares().value().toString()
            ,
            requirement.priceType() == PriceType.LIMITED ? ((LimitedPriceTradeRequest) requirement).targetPrice()
                .unitValue().toString() : null
            ,
            order.trades().stream().map(OrderTranslator::from).collect(Collectors.toList()),
            order.status().toString()
        );
    }

    private static TradeDTO from(Trade t) {
        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setPrice(t.deal().dealtPrice().toString());
        tradeDTO.setShares(t.deal().shares().toString());
        tradeDTO.setTime(t.dealtOn());
        return tradeDTO;
    }

}
