package org.example.trade.domain.market;

public class SecurityCode {

    private final String string;

    public SecurityCode(String string) {
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
