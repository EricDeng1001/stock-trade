package org.example.trade.domain.account;

import org.jetbrains.annotations.NotNull;

public class Asset {

    private final Id id;

    public Asset(Id id) {
        this.id = id;
    }

    public Id id() {
        return id;
    }

    public Account account() {
        return id.account;
    }

    public static class Id {

        private final Account account;

        public Id(@NotNull Account account) {
            this.account = account;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            return account.equals(((Id) o).account);
        }

        @Override
        public int hashCode() {
            return account.hashCode();
        }

    }

}
