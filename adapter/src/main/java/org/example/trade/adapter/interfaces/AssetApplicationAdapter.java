package org.example.trade.adapter.interfaces;

import org.example.trade.adapter.interfaces.translator.AccountIdTranslator;
import org.example.trade.adapter.interfaces.translator.AssetTranslator;
import org.example.trade.application.AssetService;
import org.example.trade.interfaces.account.AssetApplication;
import org.example.trade.interfaces.account.AssetDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class AssetApplicationAdapter implements AssetApplication {

    private final AssetService assetService;

    public AssetApplicationAdapter(AssetService assetService) {this.assetService = assetService;}

    @Override
    @GetMapping("/{id}")
    public AssetDTO queryAsset(@PathVariable("id") String accountId) {
        return AssetTranslator.from(
            assetService.queryAsset(
                AccountIdTranslator.from(accountId)
            )
        );
    }

    @Override
    @PostMapping("/sync")
    public void syncAsset(@RequestBody String accountId) {
        assetService.syncAssetFromBroker(AccountIdTranslator.from(accountId));
    }

}
