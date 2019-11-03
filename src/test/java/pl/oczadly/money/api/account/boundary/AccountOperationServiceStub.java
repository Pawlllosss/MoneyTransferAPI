package pl.oczadly.money.api.account.boundary;

import pl.oczadly.money.api.account.AccountTestUtils;
import pl.oczadly.money.api.account.control.AccountOperationService;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.dto.AccountTransferDTO;
import pl.oczadly.money.api.account.entity.exception.IncorrectAccountOperationAmount;
import pl.oczadly.money.api.account.entity.exception.InsufficientFundsException;
import pl.oczadly.money.api.client.ClientTestUtils;
import pl.oczadly.money.api.client.entity.Client;

import java.math.BigDecimal;
import java.util.Set;

import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_1_BALANCE;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_1_ID;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_2_BALANCE;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.CLIENT_FIRST_NAME;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.CLIENT_SURNAME;
import static pl.oczadly.money.api.account.control.AccountUtils.convertDoubleToBigDecimal;

public class AccountOperationServiceStub implements AccountOperationService {

    private BigDecimal WITHDRAW_THRESHOLD = new BigDecimal("99999999");

    @Override
    public Account withdrawMoney(AccountOperationDTO accountOperationDTO) {
        BigDecimal amount = getOperationAmount(accountOperationDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);

        if (!isEnoughFundsToPerformWithdraw(account, amount)) {
            throw new InsufficientFundsException(ACCOUNT_1_ID);
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

    private boolean isEnoughFundsToPerformWithdraw(Account account, BigDecimal amount) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.compareTo(WITHDRAW_THRESHOLD) >= 0;
    }

    @Override
    public Account depositMoney(AccountOperationDTO accountOperationDTO) {
        BigDecimal amount = getOperationAmount(accountOperationDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        return AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
    }

    @Override
    public Set<Account> transferMoney(AccountTransferDTO accountTransferDTO) {
        BigDecimal amount = getTransferAmount(accountTransferDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        return Set.of(
                AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client),
                AccountTestUtils.createAccountEntity(ACCOUNT_2_BALANCE, client)
        );

    }

    private BigDecimal getTransferAmount(AccountTransferDTO accountTransferDTO) {
        Double amountAsDouble = accountTransferDTO.getAmount();
        return convertDoubleToBigDecimal(amountAsDouble);
    }

}
