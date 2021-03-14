package org.example.trade.domain.account;

import org.example.trade.domain.market.Broker;
import org.example.trade.domain.order.Deal;
import org.example.trade.domain.order.Order;

public class Account {

    private final Id id;

    private final Asset asset;

    private String password;

    public Account(Broker broker, String brokerId, String password, Asset.Builder builder) {
        this.id = new Id(broker, brokerId);
        this.asset = builder.withId(id).build();
        this.password = password;
    }

    public String password() {
        return password;
    }

    public Asset asset() {
        return asset;
    }

    public Id id() {
        return id;
    }

    public Broker broker() {
        return id.broker;
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", asset=" + asset +
            '}';
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public Asset.CashResource getCashLock(Order order) {
        return asset.getCashLock(order);
    }

    public Asset.SharesResource getSharesLock(Order order) {
        return asset.getSharesLock(order);
    }

    public void exchange(Order.Id order, Deal deal) {

    }

    public void unFreeze(Order.Id id) {

    }

    public void finishOrder(Order.Id id) {

    }

    public static class Id {

        private final Broker broker;

        private final String brokerId;

        public Id(Broker broker, String brokerId) {
            this.broker = broker;
            this.brokerId = brokerId;
        }

        public Broker broker() {
            return broker;
        }

        public String brokerId() {
            return brokerId;
        }

        @Override
        public String toString() {
            return "Id{" +
                "broker=" + broker +
                ", brokerId='" + brokerId + '\'' +
                '}';
        }

    }

}
