package account.control;

import account.entity.Account;
import account.entity.dto.AccountCreateDTO;
import account.entity.dto.AccountOperationDTO;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountCreateDTO accountCreateDTO);

    List<Account> getAllAccount();

    Account getAccountById(Long id);

    Account withdrawMoney(AccountOperationDTO accountOperationDTO);

    Account depositMoney(AccountOperationDTO accountOperationDTO);

    void deleteAccount(Long id);

}
