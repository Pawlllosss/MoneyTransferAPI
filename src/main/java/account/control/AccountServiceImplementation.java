package account.control;

import account.entity.Account;
import account.entity.dto.AccountCreateDTO;
import account.entity.dto.AccountOperationDTO;
import client.control.ClientService;
import client.entity.Client;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class AccountServiceImplementation implements AccountService {

    private AccountDAO accountDAO;

    private ClientService clientService;

    @Inject
    public AccountServiceImplementation(AccountDAO accountDAO, ClientService clientService) {
        this.accountDAO = accountDAO;
        this.clientService = clientService;
    }

    @Override
    public Account createAccount(AccountCreateDTO accountCreateDTO) {
        Long clientId = accountCreateDTO.getClientId();
        Client client = clientService.getClientById(clientId);

        Account account = mapAccountCreateDTOToAccount(accountCreateDTO);
        client.addAccount(account);
        clientService.updateClient(clientId, client);

        return account;
    }

    private Account mapAccountCreateDTOToAccount(AccountCreateDTO accountCreateDTO) {
        Double balanceAsDouble = accountCreateDTO.getBalance();
        BigDecimal balance = convertDoubleToBigDecimal(balanceAsDouble);

        Account account = new Account();
        account.setBalance(balance);

        return account;
    }

    private BigDecimal convertDoubleToBigDecimal(Double value) {
        String valueAsString = value.toString();
        return new BigDecimal(valueAsString);
    }

    @Override
    public List<Account> getAllAccount() {
        return null;
    }

    @Override
    public Account getAccountById(Long id) {
        return null;
    }

    @Override
    public Account withdrawMoney(AccountOperationDTO accountOperationDTO) {
        return null;
    }

    @Override
    public Account depositMoney(AccountOperationDTO accountOperationDTO) {
        return null;
    }

    @Override
    public void deleteAccount(Long id) {

    }
}
