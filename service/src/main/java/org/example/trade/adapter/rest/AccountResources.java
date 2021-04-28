package org.example.trade.adapter.rest;

import org.example.trade.adapter.rest.translator.AccountIdTranslator;
import org.example.trade.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountResources {

    private final AccountService accountService;

    @Autowired
    public AccountResources(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/activate")
    public boolean activateAccount(
        @RequestHeader String account,
        @RequestBody String config
    ) {
        return accountService.activateAccount(
            AccountIdTranslator.from(account),
            config
        );
    }

    @GetMapping("/")
    public List<String> querySupportedAccounts() {
        return accountService.get().stream().map(account -> AccountIdTranslator.from(account.id()))
            .collect(Collectors.toList());
    }

    @PatchMapping("/")
    public void changeConfig(@RequestHeader String account, @RequestBody String config) {
        accountService.changeConfig(AccountIdTranslator.from(account), config);
    }

}
