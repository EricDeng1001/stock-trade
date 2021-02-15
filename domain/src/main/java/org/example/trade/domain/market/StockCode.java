package org.example.trade.domain.market;

public class StockCode {

    private final String string;

    public StockCode(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "StockCode{" +
            string +
            '}';
    }

    public String string() {
        return string;
    }

}
