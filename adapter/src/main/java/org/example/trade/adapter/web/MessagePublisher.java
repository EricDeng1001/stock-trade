package org.example.trade.adapter.web;

import org.example.trade.adapter.messaging.WebSocketNotificationService;
import org.example.trade.adapter.usecase.translator.OrderIdTranslator;
import org.example.trade.domain.order.OrderClosed;
import org.example.trade.domain.order.OrderTraded;
import org.example.trade.interfaces.message.OrderClosedMessage;
import org.example.trade.interfaces.message.OrderTradedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessagePublisher implements WebSocketNotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MessagePublisher(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    @Override
    public void orderTraded(OrderTraded orderTraded) {
        OrderTradedMessage message = new OrderTradedMessage();
        message.setOrderId(OrderIdTranslator.from(orderTraded.orderId()));
        message.setPrice(orderTraded.deal().dealtPrice().value().toString());
        message.setShares(orderTraded.deal().shares().value().toString());
        simpMessagingTemplate.convertAndSend("/topic/order-traded", message);
    }

    @Override
    public void orderClosed(OrderClosed orderClosed) {
        OrderClosedMessage message = new OrderClosedMessage();
        message.setOrderId(OrderIdTranslator.from(orderClosed.orderId()));
        message.setStatus(orderClosed.orderStatus().toString());
        simpMessagingTemplate.convertAndSend("/topic/order-finished", message);
    }

}
