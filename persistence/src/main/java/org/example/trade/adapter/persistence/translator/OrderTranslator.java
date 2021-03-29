package org.example.trade.adapter.persistence.translator;

import org.example.finance.domain.Price;
import org.example.trade.adapter.persistence.model.account.AccountId;
import org.example.trade.adapter.persistence.model.order.Order;
import org.example.trade.adapter.persistence.model.order.OrderId;
import org.example.trade.adapter.persistence.model.order.Trade;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.PriceType;
import org.example.trade.domain.order.TradeSide;
import org.example.trade.domain.order.request.LimitedPriceTradeRequest;
import org.example.trade.domain.order.request.MarketTradeRequest;
import org.example.trade.domain.order.request.TradeRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Collectors;

public class OrderTranslator {

    public static org.example.trade.domain.order.Order from(Order record) {
        TradeRequest requirement;
        TradeSide tradeSide;
        if (record.getShares().compareTo(BigInteger.ZERO) > 0) {
            tradeSide = TradeSide.BUY;
        } else {
            tradeSide = TradeSide.SELL;
        }
        SecurityCode securityCode = SecurityCode.valueOf(record.getStockCode());
        Shares shares = Shares.valueOf(record.getShares().abs());
        BigDecimal price = record.getPrice();
        if (price.compareTo(BigDecimal.ZERO) == 0) {
            requirement = new MarketTradeRequest(
                securityCode,
                shares,
                tradeSide
            );
        } else {
            requirement = new LimitedPriceTradeRequest(
                securityCode,
                shares,
                tradeSide,
                Price.valueOf(price)
            );
        }
        org.example.trade.domain.order.Order order = new org.example.trade.domain.order.Order(
            from(record.getId()),
            requirement,
            record.getStatus(),
            record.getTrades().stream().map(OrderTranslator::from).collect(Collectors.toList()),
            record.getBrokerId(),
            record.getCreatedAt(),
            record.getSubmittedAt(),
            record.getClosedAt()
        );
        order.setVersion(record.getVersion());
        return order;
    }

    public static org.example.trade.domain.order.OrderId from(OrderId id) {
        return new org.example.trade.domain.order.OrderId(
            AccountTranslator.from(id.getAccountId()),
            id.getTradeDay(),
            id.getOrderId()
        );
    }

    public static Order from(org.example.trade.domain.order.Order order) {
        Order record = new Order();
        record.setId(from(order.id()));
        record.setBrokerId(order.brokerId());
        record.setClosedAt(order.closedAt());
        record.setCreatedAt(order.createdAt());
        record.setSubmittedAt(order.submittedAt());
        TradeRequest requirement = order.requirement();
        record.setStockCode(requirement.securityCode().value());
        record.setShares(switch (requirement.tradeSide()) {
            case BUY -> requirement.shares().value();
            case SELL -> requirement.shares().value().negate();
        });
        record.setPrice(
            requirement.priceType() == PriceType.LIMITED ? ((LimitedPriceTradeRequest) requirement)
                .targetPrice().unitValue().value() : BigDecimal.ZERO);
        record.setStatus(order.status());
        record.setTrades(order.trades().stream().map(OrderTranslator::from).collect(Collectors.toList()));
        record.setVersion(order.version());
        return record;
    }

    private static Trade from(org.example.trade.domain.order.Trade trade) {
        Trade record = new Trade();
        record.setTradeBrokerId(trade.brokerId());
        record.setPrice(trade.deal().dealtPrice().unitValue().value());
        record.setShares(trade.deal().shares().value());
        record.setDealtOn(trade.dealtOn());
        return record;
    }

    private static org.example.trade.domain.order.Trade from(Trade trade) {
        return new org.example.trade.domain.order.Trade(
            trade.getTradeBrokerId(),
            new Deal(
                Shares.valueOf(trade.getShares()),
                Price.valueOf(trade.getPrice())
            ),
            trade.getDealtOn()
        );
    }

    public static OrderId from(org.example.trade.domain.order.OrderId id) {
        OrderId idRecord = new OrderId();
        idRecord.setAccountId(from(id.accountId()));
        idRecord.setTradeDay(id.tradeDay());
        idRecord.setOrderId(id.uid());
        return idRecord;
    }

    private static AccountId from(org.example.trade.domain.account.AccountId accountId) {
        return new AccountId(accountId);
    }

}
