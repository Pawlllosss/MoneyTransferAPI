package client.control;

import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static client.ClientTestUtils.createClientEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplementationTest {

    private static final String CLIENT_1_FIRST_NAME = "Paul";
    private static final String CLIENT_1_SURNAME = "Smith";
    private static final String CLIENT_2_FIRST_NAME = "Rick";
    private static final String CLIENT_2_SURNAME = "Sanchez";
    private static final String CLIENT_3_FIRST_NAME = "Homer";
    private static final String CLIENT_3_SURNAME = "Simpson";
    private static final Long CLIENT_1_ID = 1L;
    private static final Long NOT_EXISTING_ID = 111L;

    private ClientServiceImplementation clientService;

    @Mock
    private ClientDAO clientDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        clientService = new ClientServiceImplementation(clientDAO);
    }

    @Test
    void shouldCallClientDAOCreateOnceWhenTryingToCreateClient() {
        Client client = new Client();
        client.setFirstName(CLIENT_1_FIRST_NAME);

        clientService.createClient(client);

        verifyClientDAOCreateCalledOnce(client);
    }

    private void verifyClientDAOCreateCalledOnce(Client client) {
        verify(clientDAO, times(1)).create(client);
    }


    @Test
    void shouldReturnAllClientsWhenTryingToGetAllClients() {
        mockClientDAOGetAll();

        List<Client> clientsFromService = clientService.getAllClients();

        verifyClientDAOGetAllCalledOnce();
        final int expectedClientsSize = 3;
        assertThat(clientsFromService).hasSize(expectedClientsSize);
        assertThat(clientsFromService).extracting(Client::getFirstName)
                .containsOnly(CLIENT_1_FIRST_NAME, CLIENT_2_FIRST_NAME, CLIENT_3_FIRST_NAME);
        assertThat(clientsFromService).extracting(Client::getSurname)
                .containsOnly(CLIENT_1_SURNAME, CLIENT_2_SURNAME, CLIENT_3_SURNAME);

    }

    private void mockClientDAOGetAll() {
        Client client1 = createClientEntity(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);
        Client client2 = createClientEntity(CLIENT_2_FIRST_NAME, CLIENT_2_SURNAME);
        Client client3 = createClientEntity(CLIENT_3_FIRST_NAME, CLIENT_3_SURNAME);
        List<Client> allClients = List.of(client1, client2, client3);

        given(clientDAO.getAll()).willReturn(allClients);
    }

    private void verifyClientDAOGetAllCalledOnce() {
        verify(clientDAO, times(1)).getAll();
    }

    @Test
    void shouldReturnClientWhenTryingToGetClientByExistingId() {
        mockClientDAOGetById();

        Client clientFromService = clientService.getClientById(CLIENT_1_ID);

        verifyClientDAOGetByIdCalledOnce(CLIENT_1_ID);
        assertThat(clientFromService.getId()).isEqualTo(CLIENT_1_ID);
        assertThat(clientFromService.getFirstName()).isEqualTo(CLIENT_1_FIRST_NAME);
        assertThat(clientFromService.getSurname()).isEqualTo(CLIENT_1_SURNAME);
    }

    private void mockClientDAOGetById() {
        Client client = createClientEntity(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);
        client.setId(CLIENT_1_ID);
        given(clientDAO.getById(CLIENT_1_ID)).willReturn(Optional.of(client));
    }

    private void verifyClientDAOGetByIdCalledOnce(Long clientId) {
        verify(clientDAO, times(1)).getById(clientId);
    }

    @Test
    void shouldThrowClientDoesNotExistExceptionWhenTryingToGetClientByNotExistingId() {
        assertThrows(ClientDoesNotExistException.class, () -> clientService.getClientById(NOT_EXISTING_ID));
    }

    @Test
    void shouldCallClientDAOUpdateOnceWhenTryingToUpdateClient() {
        Client client = new Client();
        client.setFirstName(CLIENT_1_FIRST_NAME);
        client.setSurname(CLIENT_1_SURNAME);

        clientService.updateClient(CLIENT_1_ID, client);

        verifyClientDAOUpdateCalledOnce(CLIENT_1_ID, client);
    }

    private void verifyClientDAOUpdateCalledOnce(Long id, Client client) {
        verify(clientDAO, times(1)).update(id, client);
    }

    @Test
    void shouldCallClientDAODeleteOnceWhenTryingToDeleteClient() {
        clientService.deleteClient(CLIENT_1_ID);

        verifyClientDAODeleteCalledOnce(CLIENT_1_ID);
    }

    private void verifyClientDAODeleteCalledOnce(Long id) {
        verify(clientDAO, times(1)).delete(id);
    }
}