package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.dto.AccountTransferDTO;

import java.util.Set;

public interface AccountOperationService {

    Account withdrawMoney(AccountOperationDTO accountOperationDTO);

    Account depositMoney(AccountOperationDTO accountOperationDTO);

    Set<Account> transferMoney(AccountTransferDTO accountTransferDTO);
}
