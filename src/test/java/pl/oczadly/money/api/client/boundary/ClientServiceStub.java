package pl.oczadly.money.api.client.boundary;

import pl.oczadly.money.api.client.ClientTestUtils;
import pl.oczadly.money.api.client.control.ClientService;
import pl.oczadly.money.api.client.entity.Client;
import pl.oczadly.money.api.client.entity.exception.ClientDoesNotExistException;

import java.util.List;

public class ClientServiceStub implements ClientService {

    @Override
    public Client createClient(Client client) {
        client.setId(ClientControllerTest.CLIENT_1_ID);
        return client;
    }

    @Override
    public List<Client> getAllClients() {
        Client client1 = ClientTestUtils.createClientEntity(ClientControllerTest.CLIENT_1_FIRST_NAME, ClientControllerTest.CLIENT_1_SURNAME);
        client1.setId(ClientControllerTest.CLIENT_1_ID);
        Client client2 = ClientTestUtils.createClientEntity(ClientControllerTest.CLIENT_2_FIRST_NAME, ClientControllerTest.CLIENT_2_SURNAME);
        client2.setId(ClientControllerTest.CLIENT_2_ID);

        return List.of(client1, client2);
    }

    @Override
    public Client getClientById(Long id) {
        if (!id.equals(ClientControllerTest.CLIENT_1_ID)) {
            throw new ClientDoesNotExistException(id);
        }

        Client client = ClientTestUtils.createClientEntity(ClientControllerTest.CLIENT_1_FIRST_NAME, ClientControllerTest.CLIENT_1_SURNAME);
        client.setId(id);
        return client;
    }

    @Override
    public Client updateClient(Long id, Client client) {
        if (!id.equals(ClientControllerTest.CLIENT_1_ID)) {
            throw new ClientDoesNotExistException(id);
        }

        client.setId(id);
        return client;
    }

    @Override
    public void deleteClient(Long id) {
        if (!id.equals(ClientControllerTest.CLIENT_1_ID)) {
            throw new ClientDoesNotExistException(id);
        }
    }
}
