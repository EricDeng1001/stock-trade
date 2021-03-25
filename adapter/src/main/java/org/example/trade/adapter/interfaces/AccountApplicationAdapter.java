package org.example.trade.adapter.interfaces;

import org.example.trade.adapter.interfaces.translator.AccountIdTranslator;
import org.example.trade.application.AccountService;
import org.example.trade.interfaces.account.AccountApplication;
import org.example.trade.interfaces.account.AccountDTO;
import org.example.trade.interfaces.account.ActivateAccountCommand;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountApplicationAdapter implements AccountApplication {

    private final AccountService accountService;

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
    public AccountDTO queryAccount(String accountId) {
        return null;
    }

    @Override
    @GetMapping("/")
    public List<String> queryAccounts() {
        return accountService.getAll().stream().map(account -> AccountIdTranslator.from(account.id()))
            .collect(Collectors.toList());
    }

    @Override
    public AccountDTO changeConfig(String accountId, String config) {
        return null;
    }

}
