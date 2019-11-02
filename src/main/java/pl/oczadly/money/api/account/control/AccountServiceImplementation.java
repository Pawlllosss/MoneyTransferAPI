package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;
import pl.oczadly.money.api.client.control.ClientService;
import pl.oczadly.money.api.client.entity.Client;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static pl.oczadly.money.api.account.control.AccountUtils.convertDoubleToBigDecimal;

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

    @Override
    public List<Account> getAllAccounts() {
        return accountDAO.getAll();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountDAO.getById(id)
                .orElseThrow(() -> new AccountDoesNotExistException(id));
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        Client client = account.getClient();

        client.removeAccount(account);
        clientService.updateClient(client.getId(), client);
    }
}
