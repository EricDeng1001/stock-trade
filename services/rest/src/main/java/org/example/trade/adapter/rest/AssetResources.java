package org.example.trade.adapter.rest;

import org.example.trade.adapter.rest.boundary.AssetDTO;
import org.example.trade.adapter.rest.translator.AccountIdTranslator;
import org.example.trade.adapter.rest.translator.AssetTranslator;
import org.example.trade.application.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class AssetResources {

    private final AssetService assetService;

    @Autowired
    public AssetResources(AssetService assetService) {this.assetService = assetService;}

    @GetMapping("/{id}")
    public AssetDTO queryAsset(@PathVariable("id") String accountId) {
        return AssetTranslator.from(
            assetService.queryAsset(
                AccountIdTranslator.from(accountId)
            )
        );
    }

    @PostMapping("/sync")
    public void syncAsset(@RequestBody String accountId) {
        if (accountId == null) { throw new IllegalArgumentException(); }
        assetService.syncAssetFromBroker(AccountIdTranslator.from(accountId));
    }

}
