package org.example.trade.adapter.interfaces;

import org.example.trade.adapter.interfaces.translator.AccountIdTranslator;
import org.example.trade.application.AccountService;
import org.example.trade.interfaces.account.AccountApplication;
import org.example.trade.interfaces.account.ActivateAccountCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountApplicationAdapter implements AccountApplication {

    private final AccountService accountService;

    @Autowired
    public AccountApplicationAdapter(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    @PostMapping("/activate")
    public boolean activateAccount(@RequestBody ActivateAccountCommand command) {
        return accountService.activateAccount(
            AccountIdTranslator.from(command.getAccountId()),
            command.getConfig()
        );
    }

    @Override
    @GetMapping("/")
    public List<String> querySupportedAccounts() {
        return accountService.getAll().stream().map(account -> AccountIdTranslator.from(account.id()))
            .collect(Collectors.toList());
    }

    @Override
    @PatchMapping("/{accountId}")
    public void changeConfig(@PathVariable String accountId, @RequestBody String config) {
        accountService.changeConfig(AccountIdTranslator.from(accountId), config);
    }

}
