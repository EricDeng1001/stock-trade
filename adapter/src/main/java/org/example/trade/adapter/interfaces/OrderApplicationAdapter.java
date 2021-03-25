package org.example.trade.adapter.interfaces;

import org.example.trade.adapter.interfaces.translator.AccountIdTranslator;
import org.example.trade.adapter.interfaces.translator.OrderIdTranslator;
import org.example.trade.adapter.interfaces.translator.OrderTranslator;
import org.example.trade.adapter.interfaces.translator.TradeRequestTranslator;
import org.example.trade.application.OrderService;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.interfaces.order.CreateOrderCommand;
import org.example.trade.interfaces.order.OrderApplication;
import org.example.trade.interfaces.order.OrderDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderApplicationAdapter implements OrderApplication {

    private final OrderService orderService;

    public OrderApplicationAdapter(OrderService orderService) {
        this.orderService = orderService;
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
    public OrderDTO queryOrder(@PathVariable("id") String orderIdDTO) {
        OrderId orderId = OrderIdTranslator.from(orderIdDTO);
        return OrderTranslator.from(orderService.queryOrder(orderId));
    }

    @Override
    @PostMapping("/enqueue")
    public boolean enqueueOrder(@RequestBody String orderIdDTO) {
        return orderService.enqueueOrder(OrderIdTranslator.from(orderIdDTO));
    }

    @Override
    @PostMapping("/enqueue/all")
    public boolean enqueueAll(@RequestBody String accountId) {
        return orderService.enqueueAll(AccountIdTranslator.from(accountId));
    }

    @Override
    @PostMapping("/dequeue")
    public boolean dequeueOrder(@PathVariable String orderIdDTO) {
        return orderService.dequeueOrder(OrderIdTranslator.from(orderIdDTO));
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
