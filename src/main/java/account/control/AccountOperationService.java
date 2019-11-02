package account.control;

import account.entity.Account;
import account.entity.dto.AccountOperationDTO;
import account.entity.dto.AccountTransferDTO;

import java.util.Set;

public interface AccountOperationService {

    Account withdrawMoney(AccountOperationDTO accountOperationDTO);

    Account depositMoney(AccountOperationDTO accountOperationDTO);

    Set<Account> transferMoney(AccountTransferDTO accountTransferDTO);
}
