package org.example.trade.adapter.application;

import org.example.trade.application.OrderService;
import org.example.trade.interfaces.order.CreateOrderCommand;
import org.example.trade.interfaces.order.OrderApplication;
import org.example.trade.interfaces.order.OrderDTO;

public class OrderApplicationAdapter implements OrderApplication {

    private final OrderService orderService;

    public OrderApplicationAdapter(OrderService orderService) {this.orderService = orderService;}

    @Override
    public String createOrder(CreateOrderCommand command) {
        return null;
    }

    @Override
    public OrderDTO queryOrder(String orderIdDTO) {
        return null;
    }

    @Override
    public void enqueueOrder(String orderIdDTO) {

    }

    @Override
    public void dequeueOrder(String orderIdDTO) {

    }

}
