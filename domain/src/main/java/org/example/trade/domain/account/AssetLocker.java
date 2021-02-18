package org.example.trade.domain.account;

import java.util.Objects;

public abstract class AssetLocker {

    @Override
    public int hashCode() {
        return Objects.hashCode(assetLockerTrackId());
    }

    protected abstract String assetLockerTrackId();

}
