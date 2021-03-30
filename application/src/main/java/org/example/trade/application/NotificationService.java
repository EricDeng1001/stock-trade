package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEvent;
import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import engineering.ericdeng.architecture.domain.model.DomainEventSubscriber;
import org.example.trade.domain.order.OrderClosed;
import org.example.trade.domain.order.OrderTraded;
import org.example.trade.infrastructure.messaging.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService extends DomainEventSubscriber<DomainEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public NotificationService(
        WebSocketNotificationService webSocketNotificationService) {
        this.webSocketNotificationService = webSocketNotificationService;
        DomainEventBus.instance().subscribe(this);
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        logger.info("准备推送事件");
        if (domainEvent instanceof OrderTraded) {
            webSocketNotificationService.orderTraded((OrderTraded) domainEvent);
        } else if (domainEvent instanceof OrderClosed) {
            webSocketNotificationService.orderClosed((OrderClosed) domainEvent);
        }
    }

}