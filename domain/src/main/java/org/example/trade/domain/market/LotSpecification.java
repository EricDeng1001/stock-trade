package org.example.trade.domain.market;

import org.example.trade.domain.order.TradeSide;
import org.example.trade.domain.order.request.TradeRequest;

public class LotSpecification {

    private final int lotValue;

    private final Shares sells;

    public LotSpecification(int lotValue, Shares sells) {
        this.lotValue = lotValue;
        this.sells = sells;
    }

    public boolean isSatisfiedBy(TradeRequest tradeRequest) {
        Shares shares = tradeRequest.shares();
        if (shares.canBeDividedBy(lotValue)) { return true; }
        return tradeRequest.tradeSide() == TradeSide.SELL && shares == sells;
    }

}
