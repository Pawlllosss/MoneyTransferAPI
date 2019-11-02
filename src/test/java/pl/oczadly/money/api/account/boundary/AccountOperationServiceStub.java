package pl.oczadly.money.api.account.boundary;

import pl.oczadly.money.api.account.control.AccountOperationService;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.dto.AccountTransferDTO;

import java.util.Set;

public class AccountOperationServiceStub implements AccountOperationService {

    @Override
    public Account withdrawMoney(AccountOperationDTO accountOperationDTO) {
        return null;
    }

    @Override
    public Account depositMoney(AccountOperationDTO accountOperationDTO) {
        return null;
    }

    @Override
    public Set<Account> transferMoney(AccountTransferDTO accountTransferDTO) {
        return null;
    }
}
