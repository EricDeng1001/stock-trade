package org.example.trade.adapter.rest.translator;

import org.example.trade.adapter.rest.boundary.AssetDTO;
import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;

import java.util.HashMap;
import java.util.Map;

public class AssetTranslator {

    public static AssetDTO from(Asset asset) {
        if (asset == null) return null;
        AssetDTO dto = new AssetDTO();
        dto.setUsableCash(asset.usableCash().toString());
        HashMap<String, String> positions = new HashMap<>();
        for (Map.Entry<SecurityCode, Shares> e : asset.usablePositions().entrySet()) {
            positions.put(e.getKey().value(), e.getValue().toString());
        }
        dto.setUsablePositions(positions);
        return dto;
    }

}
