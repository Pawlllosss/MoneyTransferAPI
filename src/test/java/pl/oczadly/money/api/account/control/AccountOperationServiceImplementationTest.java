package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.AccountTestUtils;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;
import pl.oczadly.money.api.account.entity.exception.IncorrectAccountOperationAmount;
import pl.oczadly.money.api.account.entity.exception.InsufficientFundsException;
import pl.oczadly.money.api.client.ClientTestUtils;
import pl.oczadly.money.api.client.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AccountOperationServiceImplementationTest {

    private static final BigDecimal ACCOUNT_1_BALANCE = new BigDecimal("100");
    private static final BigDecimal ACCOUNT_2_BALANCE = new BigDecimal("200");
    private static final Long ACCOUNT_1_ID = 1L;
    private static final Long NOT_EXISTING_ACCOUNT_ID = 111L;
    private static final String CLIENT_FIRST_NAME = "Paul";
    private static final String CLIENT_SURNAME = "Smith";
    private static final Long CLIENT_ID = 1L;
    private static final Long NOT_EXISTING_CLIENT_ID = 111L;

    private AccountOperationServiceImplementation accountOperationService;

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        accountOperationService = new AccountOperationServiceImplementation(accountDAO, accountService);
    }

    @Test
    void shouldCallAccountServiceGetAccountByIdAccountDAOUpdateWhenTryingToWithdrawMoney() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountServiceGetById(account);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, ACCOUNT_1_BALANCE.doubleValue());

        accountOperationService.withdrawMoney(accountOperationDTO);

        verifyAccountServiceGetAccountByIdCalledOnce(ACCOUNT_1_ID);
        verifyAccountDAOUpdateCalledOnce(ACCOUNT_1_ID, account);
    }

    private void mockAccountServiceGetById(Account account) {
        given(accountService.getAccountById(ACCOUNT_1_ID)).willReturn(account);

    }

    private void verifyAccountServiceGetAccountByIdCalledOnce(Long id) {
        verify(accountService, times(1)).getAccountById(id);
    }

    private void verifyAccountDAOUpdateCalledOnce(Long id, Account account) {
        verify(accountDAO, times(1)).update(id, account);
    }


    private AccountOperationDTO createAccountOperationDTO(Long id, double amount) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        accountOperationDTO.setId(id);
        accountOperationDTO.setAmount(amount);

        return accountOperationDTO;
    }

    @Test
    void shouldThrowAccountDoesNotExistExceptionWhenTryingToWithdrawMoneyByNotExistingId() {
        mockAccountServiceGetAccountByIdToThrowAccountDoesNotExistException(NOT_EXISTING_ACCOUNT_ID);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(NOT_EXISTING_ACCOUNT_ID, 1);

        assertThrows(AccountDoesNotExistException.class, () -> accountOperationService.withdrawMoney(accountOperationDTO));
    }

    private void mockAccountServiceGetAccountByIdToThrowAccountDoesNotExistException(Long notExistingAccountId) {
        given(accountService.getAccountById(notExistingAccountId)).willThrow(new AccountDoesNotExistException(notExistingAccountId));
    }

    @Test
    void shouldThrowIncorrectAccountOperationAmountWhenTryingToWithdrawZeroMoney() {
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, 0);

        assertThrows(IncorrectAccountOperationAmount.class, () -> accountOperationService.withdrawMoney(accountOperationDTO));
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenTryingToWithdrawMoneyFromAccountWithInsufficientFunds() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        BigDecimal insufficientBalance = ACCOUNT_1_BALANCE.subtract(new BigDecimal("1"));
        Account account = AccountTestUtils.createAccountEntity(insufficientBalance, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountServiceGetById(account);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, ACCOUNT_1_BALANCE.doubleValue());

        assertThrows(InsufficientFundsException.class, () -> accountOperationService.withdrawMoney(accountOperationDTO));
    }

    @Test
    void shouldCallAccountDAOUpdateOnceWhenTryingToDepositMoney() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountServiceGetById(account);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, 1);

        accountOperationService.depositMoney(accountOperationDTO);

        verifyAccountDAOUpdateCalledOnce(ACCOUNT_1_ID, account);
    }

    @Test
    void shouldThrowAccountDoesNotExistExceptionWhenTryingToDepositMoneyByNotExistingId() {
        mockAccountServiceGetAccountByIdToThrowAccountDoesNotExistException(NOT_EXISTING_ACCOUNT_ID);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(NOT_EXISTING_ACCOUNT_ID, 1);

        assertThrows(AccountDoesNotExistException.class, () -> accountOperationService.depositMoney(accountOperationDTO));
    }

    @Test
    void shouldThrowIncorrectAccountOperationAmountWhenTryingToDepositZeroMoney() {
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, 0);

        assertThrows(IncorrectAccountOperationAmount.class, () -> accountOperationService.depositMoney(accountOperationDTO));
    }
}