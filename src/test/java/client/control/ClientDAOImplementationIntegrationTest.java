package client.control;

import client.ClientTestUtils;
import client.entity.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientDAOImplementationIntegrationTest {

    private static final String CLIENT_1_FIRST_NAME = "Paul";
    private static final String CLIENT_1_SURNAME = "Smith";
    private static final String CLIENT_2_FIRST_NAME = "Rick";
    private static final String CLIENT_2_SURNAME = "Sanchez";

    private ClientDAOImplementation clientDAO;
    private EntityManager entityManager;
    private List<Client> createdClients;

    public ClientDAOImplementationIntegrationTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("money_transfer");
        entityManager = entityManagerFactory.createEntityManager();
        clientDAO = new ClientDAOImplementation(entityManager);
        createdClients = new LinkedList<>();
    }

    @BeforeEach
    void setUp() {
        createdClients = new LinkedList<>();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("money_transfer");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void clearDatabase() {
        entityManager.getTransaction().begin();
        createdClients.stream()
                .map(Client::getId)
                .map(id -> entityManager.find(Client.class, id))
                .forEach(client -> entityManager.remove(client));
        entityManager.getTransaction().commit();

        createdClients.clear();
    }

    @Test
    void shouldReturnNoClientsWhenNoClientsPersisted() {
        List<Client> retrievedClients = clientDAO.getAll();
        assertThat(retrievedClients).isEmpty();
    }

    @Test
    void shouldReturnAllCreatedClientsWhenCreatingClients() {
        Client client1 = ClientTestUtils.createClientEntity(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);
        createdClients.add(client1);
        Client client2 = ClientTestUtils.createClientEntity(CLIENT_2_FIRST_NAME, CLIENT_2_SURNAME);
        createdClients.add(client2);

        clientDAO.create(client1);
        clientDAO.create(client2);

        List<Client> retrievedClients = clientDAO.getAll();
        final int expectedClientsSize = 2;
        assertThat(retrievedClients).hasSize(expectedClientsSize);
        assertThat(retrievedClients).extracting(Client::getFirstName)
                .containsOnly(CLIENT_1_FIRST_NAME, CLIENT_2_FIRST_NAME);
        assertThat(retrievedClients).extracting(Client::getSurname)
                .containsOnly(CLIENT_1_SURNAME, CLIENT_2_SURNAME);
    }
}