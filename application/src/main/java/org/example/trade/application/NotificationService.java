package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEvent;
import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.order.OrderClosed;
import org.example.trade.domain.order.OrderTraded;
import org.example.trade.port.messaging.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public NotificationService(
        WebSocketNotificationService webSocketNotificationService) {
        this.webSocketNotificationService = webSocketNotificationService;
        DomainEventBus.instance().subscribe(DomainEvent.class, this::publish);
    }

    public void publish(DomainEvent domainEvent) {
        //        logger.info("准备推送: {}", domainEvent);
        if (domainEvent instanceof OrderTraded) {
            webSocketNotificationService.orderTraded((OrderTraded) domainEvent);
        } else if (domainEvent instanceof OrderClosed) {
            webSocketNotificationService.orderClosed((OrderClosed) domainEvent);
        }
    }

}