package org.example.trade.adapter.rest;

import org.example.trade.adapter.rest.boundary.ActivateAccountCommand;
import org.example.trade.adapter.rest.translator.AccountIdTranslator;
import org.example.trade.application.AccountService;
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
    public boolean activateAccount(@RequestBody ActivateAccountCommand command) {
        return accountService.activateAccount(
            AccountIdTranslator.from(command.getAccountId()),
            command.getConfig()
        );
    }

    @GetMapping("/")
    public List<String> querySupportedAccounts() {
        return accountService.getAll().stream().map(account -> AccountIdTranslator.from(account.id()))
            .collect(Collectors.toList());
    }

    @PatchMapping("/{accountId}")
    public void changeConfig(@PathVariable String accountId, @RequestBody String config) {
        accountService.changeConfig(AccountIdTranslator.from(accountId), config);
    }

}
