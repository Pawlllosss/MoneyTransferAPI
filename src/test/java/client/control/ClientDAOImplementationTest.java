package client.control;

import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ClientDAOImplementationTest {

    private static final String CLIENT_FIRST_NAME = "Paul";
    private static final String CLIENT_SURNAME = "Smith";
    private static final Long NOT_EXISTING_ID = 111L;

    private ClientDAOImplementation clientDAO;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientDAO = new ClientDAOImplementation(entityManager);
    }

    @Test
    void shouldThrowClientDoesNotExistExceptionWhenTryingToUpdateClientByNotExistingId() {
        Client client = new Client();
        client.setFirstName(CLIENT_FIRST_NAME);
        client.setSurname(CLIENT_SURNAME);

        assertThrows(ClientDoesNotExistException.class, () -> clientDAO.update(NOT_EXISTING_ID, client));
    }

    @Test
    void shouldThrowClientDoesNotExistExceptionWhenTryingToDeleteClientByNotExistingId() {
        assertThrows(ClientDoesNotExistException.class, () -> clientDAO.delete(NOT_EXISTING_ID));
    }

}