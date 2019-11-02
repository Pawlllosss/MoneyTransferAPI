package account.control;

import account.entity.Account;
import account.entity.dto.AccountCreateDTO;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountCreateDTO accountCreateDTO);

    List<Account> getAllAccounts();

    Account getAccountById(Long id);

    void deleteAccount(Long id);

}
