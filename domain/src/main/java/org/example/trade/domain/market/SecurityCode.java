package org.example.trade.domain.market;

public class SecurityCode {

    // TODO regex check
    private final String string;

    private SecurityCode(String string) {
        this.string = string;
    }

    public static SecurityCode valueOf(String s) {
        return new SecurityCode(s);
    }

    @Override
    public String toString() {
        return string;
    }

}
