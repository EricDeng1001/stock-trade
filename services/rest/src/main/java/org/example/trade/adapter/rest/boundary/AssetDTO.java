package org.example.trade.adapter.rest.boundary;

import java.util.Map;

public class AssetDTO {

    private String usableCash;

    private Map<String, String> usablePositions;

    public String getUsableCash() {
        return usableCash;
    }

    public void setUsableCash(String usableCash) {
        this.usableCash = usableCash;
    }

    public Map<String, String> getUsablePositions() {
        return usablePositions;
    }

    public void setUsablePositions(Map<String, String> usablePositions) {
        this.usablePositions = usablePositions;
    }

}
