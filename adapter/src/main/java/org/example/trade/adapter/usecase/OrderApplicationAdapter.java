package org.example.trade.adapter.usecase;

import org.example.trade.adapter.usecase.translator.AccountIdTranslator;
import org.example.trade.adapter.usecase.translator.OrderIdTranslator;
import org.example.trade.adapter.usecase.translator.OrderTranslator;
import org.example.trade.adapter.usecase.translator.TradeRequestTranslator;
import org.example.trade.application.OrderService;
import org.example.trade.application.QueueService;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.interfaces.order.CreateOrderCommand;
import org.example.trade.interfaces.order.OrderApplication;
import org.example.trade.interfaces.order.OrderDTO;
import org.example.trade.interfaces.order.TradeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/orders")
public class OrderApplicationAdapter implements OrderApplication {

    private final OrderService orderService;

    private final QueueService queueService;

    @Autowired
    public OrderApplicationAdapter(OrderService orderService, QueueService queueService) {
        this.orderService = orderService;
        this.queueService = queueService;
    }

    @Override
    @PostMapping("/")
    public String createOrder(@RequestBody CreateOrderCommand command) {
        return OrderIdTranslator.from(
            orderService.createOrder(
                TradeRequestTranslator.from(command),
                AccountIdTranslator.from(command.getAccountId())
            ));
    }

    @Override
    @GetMapping("/{id}")
    public OrderDTO queryOrder(@PathVariable String id) {
        OrderId orderId = OrderIdTranslator.from(id);
        return OrderTranslator.from(orderService.queryOrder(orderId));
    }

    @Override
    @GetMapping("/account/{accountId}")
    public List<OrderDTO> queryOrdersOfAccount(@PathVariable String accountId) {
        return orderService.queryOrder(AccountIdTranslator.from(accountId))
            .stream().map(OrderTranslator::from)
            .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/account/{accountId}/trades")
    public List<TradeDTO> queryTradesOfAccount(@PathVariable String accountId) {
        return queryOrdersOfAccount(accountId).stream().map(
            o -> o.getTrades().stream()
        ).reduce(Stream::concat).orElse(Stream.empty()).collect(Collectors.toList());
    }

    @Override
    @PostMapping("/enqueue")
    public boolean enqueueOrder(@RequestBody String orderIdDTO) {
        return queueService.enqueue(OrderIdTranslator.from(orderIdDTO));
    }

    @Override
    @PostMapping("/enqueue/all")
    public Map<String, Boolean> enqueueAll(@RequestBody String accountId) {
        return queueService.enqueueAll(AccountIdTranslator.from(accountId)).entrySet()
            .stream().collect(Collectors.toMap(
                e -> OrderIdTranslator.from(e.getKey()),
                Map.Entry::getValue
            ));
    }

    @Override
    @PostMapping("/dequeue")
    public boolean dequeueOrder(@PathVariable String orderIdDTO) {
        return queueService.dequeue(OrderIdTranslator.from(orderIdDTO));
    }

    @Override
    @GetMapping("/")
    public Iterable<OrderDTO> getAll() {
        Iterable<Order> all = orderService.getAll();
        List<OrderDTO> result = new ArrayList<>();
        for (Order o : all) {
            result.add(OrderTranslator.from(o));
        }
        return result;
    }

}
