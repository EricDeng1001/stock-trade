package org.example.trade.domain.trade;

import java.time.Instant;

public class OrderFinished extends TradeEvent {

    protected OrderFinished(Instant occurredOn, TradeOrder.Id id, String idByBroker) {
        super(occurredOn, id, idByBroker);
    }

}
