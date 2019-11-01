package account.control;

import account.entity.dto.AccountCreateDTO;
import client.ClientTestUtils;
import client.control.ClientService;
import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplementationTest {

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
        AccountCreateDTO accountCreateDTO = createAccountCreateDTO(CLIENT_ID);

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

    private AccountCreateDTO createAccountCreateDTO(Long clientId) {
        AccountCreateDTO accountCreateDTO = new AccountCreateDTO();
        accountCreateDTO.setBalance(0d);
        accountCreateDTO.setClientId(clientId);
        return accountCreateDTO;
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
        AccountCreateDTO accountCreateDTO = createAccountCreateDTO(NOT_EXISTING_CLIENT_ID);

        assertThrows(ClientDoesNotExistException.class, () -> accountService.createAccount(accountCreateDTO));
        verifyClientServiceGetClientIdCalledOnce(NOT_EXISTING_CLIENT_ID);
    }

    private void mockClientServiceGetClientByIdToThrowClientDoesNotExistException(Long notExistingClientId) {
        given(clientService.getClientById(notExistingClientId)).willThrow(new ClientDoesNotExistException(notExistingClientId));
    }

}