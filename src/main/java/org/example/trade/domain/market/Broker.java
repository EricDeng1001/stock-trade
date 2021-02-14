package org.example.trade.domain.market;

public class Broker {

    private final Id id;

    public Broker(String id) {
        this.id = new Id(id);
    }

    public Id id() {
        return id;
    }

    @Override
    public String toString() {
        return "Broker{" +
            "id=" + id +
            '}';
    }

    public static class Id {

        private final String id;

        public Id(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return "Id{" +
                "id='" + id + '\'' +
                '}';
        }

    }

}
