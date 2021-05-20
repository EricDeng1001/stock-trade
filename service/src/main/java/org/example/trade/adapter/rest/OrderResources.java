package org.example.trade.adapter.rest;

import org.example.trade.adapter.rest.boundary.CreateOrderCommand;
import org.example.trade.adapter.rest.boundary.OrderDTO;
import org.example.trade.adapter.rest.boundary.TradeDTO;
import org.example.trade.adapter.rest.translator.AccountIdTranslator;
import org.example.trade.adapter.rest.translator.OrderIdTranslator;
import org.example.trade.adapter.rest.translator.OrderTranslator;
import org.example.trade.adapter.rest.translator.TradeRequestTranslator;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.interfaces.OrderService;
import org.example.trade.interfaces.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/orders")
public class OrderResources {

    private final OrderService orderService;

    private final QueueService queueService;

    @Autowired
    public OrderResources(OrderService orderService, QueueService queueService) {
        this.orderService = orderService;
        this.queueService = queueService;
    }

    @PostMapping("/")
    public String createOrder(@RequestHeader String account, @RequestBody CreateOrderCommand command) {
        return OrderIdTranslator.from(
            orderService.createOrder(
                TradeRequestTranslator.from(command),
                AccountIdTranslator.from(account)
            ));
    }

    @GetMapping("/{id}")
    public OrderDTO get(@PathVariable String id) {
        OrderId orderId = OrderIdTranslator.from(id);
        return OrderTranslator.from(orderService.describe(orderId));
    }

    @GetMapping("/trades")
    public List<TradeDTO> getAllTrades(@RequestHeader String account) {
        return getAll(account).stream().map(
            o -> o.getTrades().stream()
        ).reduce(Stream::concat).orElse(Stream.empty()).collect(Collectors.toList());
    }

    @GetMapping("/account")
    public List<OrderDTO> getAll(@RequestHeader String account) {
        return orderService.describeAll(AccountIdTranslator.from(account))
            .stream().map(OrderTranslator::from)
            .collect(Collectors.toList());
    }

    @PostMapping("/enqueue")
    public boolean enqueue(@RequestBody String orderId) {
        return queueService.enqueue(OrderIdTranslator.from(orderId));
    }

    @PostMapping("/enqueue/all")
    public Map<String, Boolean> enqueueAll(@RequestHeader String account) {
        return queueService.enqueueAll(AccountIdTranslator.from(account)).entrySet()
            .stream().collect(Collectors.toMap(
                e -> OrderIdTranslator.from(e.getKey()),
                Map.Entry::getValue
            ));
    }

    @PostMapping("/dequeue")
    public boolean dequeue(@RequestBody String orderId) {
        return queueService.dequeue(OrderIdTranslator.from(orderId));
    }

    @GetMapping("/")
    public Iterable<OrderDTO> list() {
        Iterable<Order> all = orderService.list();
        List<OrderDTO> result = new ArrayList<>();
        for (Order o : all) {
            result.add(OrderTranslator.from(o));
        }
        return result;
    }

}
