package org.example.trade.adapter.rest.boundary;

public class ActivateAccountCommand {

    private String accountId;

    private String config;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

}
