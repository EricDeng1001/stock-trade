package org.example.trade.application;

import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class DealService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DealService.class);

    private final OrderRepository orderRepository;

    public DealService(OrderRepository orderRepository) {this.orderRepository = orderRepository;}

    public synchronized void newDeal(OrderId id, Deal deal, Instant time) {
        // TODO transaction control, unit of work pattern, this is the main work of app layer
        Order order = orderRepository.findById(id);
        order.makeDeal(deal, time);
        orderRepository.save(order);
    }

    public synchronized void finish(OrderId id, Instant time) {
        // TODO transaction control, unit of work pattern, this is the main work of app layer
        Order order = orderRepository.findById(id);
        order.close(time);
        orderRepository.save(order);
    }

}
