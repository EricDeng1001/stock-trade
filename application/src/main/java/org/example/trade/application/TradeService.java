package org.example.trade.application;

import engineering.ericdeng.architecture.domain.model.DomainEventBus;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;
import org.example.trade.domain.order.OrderId;
import org.example.trade.domain.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    private final OrderRepository orderRepository;

    @Autowired
    public TradeService(OrderRepository orderRepository) {this.orderRepository = orderRepository;}

    @Bean
    public RetryOperationsInterceptor orderConcurrentModificationRetry() {
        RetryOperationsInterceptor retryOperationsInterceptor = new RetryOperationsInterceptor();
        retryOperationsInterceptor.setRetryOperations(
            RetryTemplate.builder()
                .retryOn(ObjectOptimisticLockingFailureException.class)
                .uniformRandomBackoff(3, 120)
                .infiniteRetry()
                .build()
        );
        return retryOperationsInterceptor;
    }

    @Retryable(interceptor = "orderConcurrentModificationRetry")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startTradingOrder(OrderId orderId, String brokerId) {
        Order order = orderRepository.findById(orderId);
        order.startTrading(brokerId);
        orderRepository.save(order);
    }

    @Retryable(interceptor = "orderConcurrentModificationRetry")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeOrder(OrderId id) {
        Order order = orderRepository.findById(id);
        order.close();
        orderRepository.save(order);
        logger.info("订单关闭交易: {}", id);
        DomainEventBus.instance().publish(order);
    }

    @Retryable(interceptor = "orderConcurrentModificationRetry")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void offerDeal(OrderId id, Deal deal, String brokerId) {
        Order order = orderRepository.findById(id);
        order.makeDeal(deal, brokerId);
        orderRepository.save(order);
        logger.info("订单取得交易: {}, 成交: {}", id, deal);
        DomainEventBus.instance().publish(order);
    }

}
