package org.example.trade.domain.order;

public class TradeCantNotBeDoneException extends Exception {

    public TradeCantNotBeDoneException(String reason) {
        super("所请求的交易不能被完成:" + reason);
    }

}
