package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.AccountTestUtils;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.dto.AccountTransferDTO;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;
import pl.oczadly.money.api.account.entity.exception.IncorrectAccountOperationAmount;
import pl.oczadly.money.api.account.entity.exception.InsufficientFundsException;
import pl.oczadly.money.api.account.entity.exception.TransferToIdenticalAccountException;
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
    private static final Long ACCOUNT_2_ID = 2L;
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
    void shouldCallAccountServiceGetAccountByIdAndAccountDAOUpdateWhenTryingToWithdrawMoney() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountServiceGetById(account, ACCOUNT_1_ID);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, ACCOUNT_1_BALANCE.doubleValue());

        accountOperationService.withdrawMoney(accountOperationDTO);

        verifyAccountServiceGetAccountByIdCalledOnce(ACCOUNT_1_ID);
        verifyAccountDAOUpdateCalledOnce(ACCOUNT_1_ID, account);
    }

    private void mockAccountServiceGetById(Account account, Long id) {
        given(accountService.getAccountById(id)).willReturn(account);

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
        mockAccountServiceGetById(account, ACCOUNT_1_ID);
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, ACCOUNT_1_BALANCE.doubleValue());

        assertThrows(InsufficientFundsException.class, () -> accountOperationService.withdrawMoney(accountOperationDTO));
    }

    @Test
    void shouldCallAccountDAOUpdateOnceWhenTryingToDepositMoney() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountServiceGetById(account, ACCOUNT_1_ID);
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


    @Test
    void shouldCallAccountServiceGetAccountByIdAndAccountDAOUpdateWhenTryingToTransferMoney() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account accountFrom = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        accountFrom.setId(ACCOUNT_1_ID);
        Account accountTo = AccountTestUtils.createAccountEntity(ACCOUNT_2_BALANCE, client);
        accountTo.setId(ACCOUNT_2_ID);
        mockAccountServiceGetById(accountFrom, ACCOUNT_1_ID);
        mockAccountServiceGetById(accountTo, ACCOUNT_2_ID);
        AccountTransferDTO accountTransferDTO = createAccountTransferDTO(ACCOUNT_1_ID, ACCOUNT_2_ID, ACCOUNT_1_BALANCE.doubleValue());

        accountOperationService.transferMoney(accountTransferDTO);

        verifyAccountServiceGetAccountByIdCalledOnce(ACCOUNT_1_ID);
        verifyAccountServiceGetAccountByIdCalledOnce(ACCOUNT_2_ID);
        verifyAccountDAOUpdateCalledOnce(accountFrom, accountTo);
    }

    private AccountTransferDTO createAccountTransferDTO(Long accountFromId, Long accountToId, double doubleValue) {
        AccountTransferDTO accountTransferDTO = new AccountTransferDTO();
        accountTransferDTO.setIdFrom(accountFromId);
        accountTransferDTO.setIdTo(accountToId);
        accountTransferDTO.setAmount(doubleValue);

        return accountTransferDTO;
    }


    private void verifyAccountDAOUpdateCalledOnce(Account account1, Account account2) {
        verify(accountDAO, times(1)).update(account1, account2);
    }

    @Test
    void shouldThrowIncorrectAccountOperationAmountWhenTryingToTransferZeroMoney() {
        AccountTransferDTO accountTransferDTO = createAccountTransferDTO(ACCOUNT_1_ID, ACCOUNT_2_ID, 0);

        assertThrows(IncorrectAccountOperationAmount.class, () -> accountOperationService.transferMoney(accountTransferDTO));
    }

    @Test
    void shouldThrowTransferToIdenticalAccountExceptionWhenTryingToTransferMoneyToSameAccount() {
        AccountTransferDTO accountTransferDTO = createAccountTransferDTO(ACCOUNT_1_ID, ACCOUNT_1_ID, 1);

        assertThrows(TransferToIdenticalAccountException.class, () -> accountOperationService.transferMoney(accountTransferDTO));
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenTryingToTransferMoneyFromAccountWithInsufficientFunds() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        BigDecimal insufficientBalance = ACCOUNT_1_BALANCE.add(new BigDecimal("1"));
        Account accountFrom = AccountTestUtils.createAccountEntity(insufficientBalance, client);
        accountFrom.setId(ACCOUNT_1_ID);
        Account accountTo = AccountTestUtils.createAccountEntity(ACCOUNT_2_BALANCE, client);
        accountTo.setId(ACCOUNT_2_ID);
        AccountTransferDTO accountTransferDTO = createAccountTransferDTO(ACCOUNT_1_ID, ACCOUNT_2_ID, ACCOUNT_1_BALANCE.doubleValue());
        mockAccountServiceGetById(accountFrom, ACCOUNT_1_ID);
        mockAccountServiceGetById(accountTo, ACCOUNT_2_ID);

        accountOperationService.transferMoney(accountTransferDTO);

        assertThrows(InsufficientFundsException.class, () -> accountOperationService.transferMoney(accountTransferDTO));
    }
}