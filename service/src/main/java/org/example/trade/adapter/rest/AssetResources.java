package org.example.trade.adapter.rest;

import org.example.trade.adapter.rest.boundary.AssetDTO;
import org.example.trade.adapter.rest.translator.AccountIdTranslator;
import org.example.trade.adapter.rest.translator.AssetTranslator;
import org.example.trade.interfaces.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class AssetResources {

    private final AssetService assetService;

    @Autowired
    public AssetResources(AssetService assetService) {this.assetService = assetService;}

    @GetMapping("/account")
    public AssetDTO queryAsset(@RequestHeader String account) {
        return AssetTranslator.from(
            assetService.queryAsset(
                AccountIdTranslator.from(account)
            )
        );
    }

    @PostMapping("/sync")
    public void syncAsset(@RequestHeader String account) {
        assetService.syncAssetFromBroker(AccountIdTranslator.from(account));
    }

}
