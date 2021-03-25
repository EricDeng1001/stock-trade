package org.example.trade.domain.account;

public class XTPAccount extends Account {

    private String password;

    private String tradeKey;

    public XTPAccount(AccountId id, String config) {
        super(id, config);
        String[] s = config.split("/");
        this.password = s[0];
        this.tradeKey = s[1];
    }

    public String username() {
        return id().brokerId();
    }

    public String password() {
        return password;
    }

    public String tradeKey() {
        return tradeKey;
    }

    @Override
    public void changeConfig(String config) {
        super.changeConfig(config);
        String[] s = config.split("/");
        this.password = s[0];
        this.tradeKey = s[1];
    }

}
