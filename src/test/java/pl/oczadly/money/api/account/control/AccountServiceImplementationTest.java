package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.AccountTestUtils;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;
import pl.oczadly.money.api.client.ClientTestUtils;
import pl.oczadly.money.api.client.control.ClientService;
import pl.oczadly.money.api.client.entity.Client;
import pl.oczadly.money.api.client.entity.exception.ClientDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static pl.oczadly.money.api.account.AccountTestUtils.createAccountCreateDTO;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplementationTest {

    private static final BigDecimal ACCOUNT_1_BALANCE = new BigDecimal("100");
    private static final BigDecimal ACCOUNT_2_BALANCE = new BigDecimal("200");
    private static final Long ACCOUNT_1_ID = 1L;
    private static final Long NOT_EXISTING_ACCOUNT_ID = 111L;
    private static final String CLIENT_FIRST_NAME = "Paul";
    private static final String CLIENT_SURNAME = "Smith";
    private static final Long CLIENT_ID = 1L;
    private static final Long NOT_EXISTING_CLIENT_ID = 111L;

    private AccountServiceImplementation accountService;

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountServiceImplementation(accountDAO, clientService);
    }

    @Test
    void shouldCallClientServiceGetByIdAndClientServiceUpdateClientWhenTryingToCreateAccountForExistingClient() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        mockClientServiceGetClientById(CLIENT_ID, client);
        mockClientServiceUpdateClient(CLIENT_ID, client);
        AccountCreateDTO accountCreateDTO = createAccountCreateDTO(0d, CLIENT_ID);

        accountService.createAccount(accountCreateDTO);

        verifyClientServiceGetClientIdCalledOnce(CLIENT_ID);
        verifyClientServiceUpdateClientCalledOnce(CLIENT_ID, client);
    }

    private void mockClientServiceGetClientById(Long clientId, Client client) {
        given(clientService.getClientById(clientId)).willReturn(client);
    }

    private void mockClientServiceUpdateClient(Long clientId, Client client) {
        given(clientService.updateClient(clientId, client)).willReturn(client);

    }

    private void verifyClientServiceGetClientIdCalledOnce(Long clientId) {
        verify(clientService, times(1)).getClientById(clientId);
    }

    private void verifyClientServiceUpdateClientCalledOnce(Long clientId, Client client) {
        verify(clientService, times(1)).updateClient(clientId, client);
    }

    @Test
    void shouldThrowClientDoesNotExistExceptionWhenTryingToCreateAccountForNotExistingClient() {
        mockClientServiceGetClientByIdToThrowClientDoesNotExistException(NOT_EXISTING_CLIENT_ID);
        AccountCreateDTO accountCreateDTO = createAccountCreateDTO(0d, NOT_EXISTING_CLIENT_ID);

        assertThrows(ClientDoesNotExistException.class, () -> accountService.createAccount(accountCreateDTO));
        verifyClientServiceGetClientIdCalledOnce(NOT_EXISTING_CLIENT_ID);
    }

    private void mockClientServiceGetClientByIdToThrowClientDoesNotExistException(Long notExistingClientId) {
        given(clientService.getClientById(notExistingClientId)).willThrow(new ClientDoesNotExistException(notExistingClientId));
    }

    @Test
    void shouldReturnAllAccountsWhenTryingToGetAllAccounts() {
        mockAccountDAOGetAll();

        List<Account> accountsFromService = accountService.getAllAccounts();

        verifyAccountDAOGetAllCalledOnce();
        final int expectedAccountsSize = 2;
        assertThat(accountsFromService).hasSize(expectedAccountsSize);
        assertThat(accountsFromService).extracting(Account::getBalance)
                .containsOnly(ACCOUNT_1_BALANCE, ACCOUNT_2_BALANCE);
        assertThat(accountsFromService).extracting(account -> account.getClient().getId())
                .containsOnly(CLIENT_ID, CLIENT_ID);
    }

    private void mockAccountDAOGetAll() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account1 = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        Account account2 = AccountTestUtils.createAccountEntity(ACCOUNT_2_BALANCE, client);
        List<Account> allAccounts = List.of(account1, account2);

        given(accountDAO.getAll()).willReturn(allAccounts);
    }

    private void verifyAccountDAOGetAllCalledOnce() {
        verify(accountDAO, times(1)).getAll();
    }

    @Test
    void shouldReturnAccountWhenTryingToGetAccountByExistingId() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountDAOGetById(account);

        Account accountFromService = accountService.getAccountById(ACCOUNT_1_ID);

        verifyAccountDAOGetByIdCalledOnce(ACCOUNT_1_ID);
        assertThat(accountFromService.getId()).isEqualTo(ACCOUNT_1_ID);
        assertThat(accountFromService.getBalance()).isEqualTo(ACCOUNT_1_BALANCE);
        assertThat(accountFromService.getClient().getId()).isEqualTo(CLIENT_ID);
    }

    private void mockAccountDAOGetById(Account account) {
        given(accountDAO.getById(ACCOUNT_1_ID)).willReturn(Optional.of(account));
    }

    private void verifyAccountDAOGetByIdCalledOnce(Long accountId) {
        verify(accountDAO, times(1)).getById(accountId);
    }

    @Test
    void shouldThrowAccountDoesNotExistExceptionWhenTryingToGetAccountByNotExistingId() {
        assertThrows(AccountDoesNotExistException.class, () -> accountService.getAccountById(NOT_EXISTING_ACCOUNT_ID));
    }

    @Test
    void shouldCallClientServiceGetByIdAndClientServiceUpdateClientWhenTryingToDeleteAccountByExistingId() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        mockClientServiceUpdateClient(CLIENT_ID, client);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);
        mockAccountDAOGetById(account);

        accountService.deleteAccount(ACCOUNT_1_ID);

        verifyAccountDAOGetByIdCalledOnce(ACCOUNT_1_ID);
        verifyClientServiceUpdateClientCalledOnce(CLIENT_ID, client);
    }
}