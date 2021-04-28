package org.example.trade.domain.market;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@EqualsAndHashCode
public class SecurityCode {

    @Getter
    private final String code;

    @Getter
    private final Market market;

    public static SecurityCode valueOf(String s) {
        return new SecurityCode(s);
    }

    private SecurityCode(String value) {
        String[] t = value.split("\\.");
        if (t.length != 2) { throw new IllegalArgumentException(); }
        code = t[0];
        market = Market.valueOf(t[1]);
    }

    public final String value() {
        return fullName();
    }

    public String fullName() {
        return code + '.' + market;
    }

    @Override
    public String toString() {
        return "SecurityCode{" +
            fullName() +
            '}';
    }

}
