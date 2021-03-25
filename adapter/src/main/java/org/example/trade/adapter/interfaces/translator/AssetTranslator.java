package org.example.trade.adapter.interfaces.translator;

import org.example.trade.domain.account.asset.Asset;
import org.example.trade.domain.market.SecurityCode;
import org.example.trade.domain.market.Shares;
import org.example.trade.interfaces.account.AssetDTO;

import java.util.HashMap;
import java.util.Map;

public class AssetTranslator {

    public static AssetDTO from(Asset asset) {
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
