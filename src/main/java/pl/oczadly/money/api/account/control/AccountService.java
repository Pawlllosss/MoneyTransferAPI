package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.account.entity.Account;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountCreateDTO accountCreateDTO);

    List<Account> getAllAccounts();

    Account getAccountById(Long id);

    void deleteAccount(Long id);
}
