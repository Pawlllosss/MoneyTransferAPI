package account.control;

import account.entity.Account;
import account.entity.dto.AccountCreateDTO;
import account.entity.dto.AccountOperationDTO;
import account.entity.exception.AccountDoesNotExistException;
import account.entity.exception.IncorrectAccountOperationAmount;
import account.entity.exception.InsufficientFundsException;
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
    public List<Account> getAllAccounts() {
        return accountDAO.getAll();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountDAO.getById(id)
                .orElseThrow(() -> new AccountDoesNotExistException(id));
    }

    @Override
    public Account withdrawMoney(AccountOperationDTO accountOperationDTO) {
        BigDecimal amount = getOperationAmount(accountOperationDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Account account = mapAccountOperationDTOToAccount(accountOperationDTO);

        synchronized (account) {
            Long accountId = account.getId();

            if (!isEnoughFundsToPerformWithdraw(account, amount)) {
                throw new InsufficientFundsException(accountId);
            }

            BigDecimal accountBalanceAfterWithdraw = getAccountBalanceAfterWithdrawal(amount, account);
            account.setBalance(accountBalanceAfterWithdraw);
            accountDAO.update(accountId, account);
        }

        return account;
    }

    private BigDecimal getOperationAmount(AccountOperationDTO accountOperationDTO) {
        Double amountAsDouble = accountOperationDTO.getAmount();
        return convertDoubleToBigDecimal(amountAsDouble);
    }

    private boolean isAmountBiggerThanZero(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) >= 1;
    }

    private Account mapAccountOperationDTOToAccount(AccountOperationDTO accountOperationDTO) {
        Long id = accountOperationDTO.getId();
        return accountDAO.getById(id)
                .orElseThrow(() -> new AccountDoesNotExistException(id));
    }

    private BigDecimal getAccountBalanceAfterWithdrawal(BigDecimal amount, Account account) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.subtract(amount);
    }

    private boolean isEnoughFundsToPerformWithdraw(Account account, BigDecimal amount) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.compareTo(amount) >= 0;
    }

    @Override
    public Account depositMoney(AccountOperationDTO accountOperationDTO) {
        BigDecimal amount = getOperationAmount(accountOperationDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Account account = mapAccountOperationDTOToAccount(accountOperationDTO);

        synchronized (account) {
            Long accountId = account.getId();

            BigDecimal accountBalanceAfterDeposit = getAccountBalanceAfterDeposit(amount, account);
            account.setBalance(accountBalanceAfterDeposit);
            accountDAO.update(accountId, account);
        }

        return account;
    }

    private BigDecimal getAccountBalanceAfterDeposit(BigDecimal amount, Account account) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.add(amount);
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        Client client = account.getClient();

        client.removeAccount(account);
        clientService.updateClient(client.getId(), client);
    }
}
