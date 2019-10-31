package client.control;

import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;

import javax.inject.Inject;
import java.util.List;

public class ClientServiceImplementation implements ClientService {

    private ClientDAO clientDAO;

    @Inject
    public ClientServiceImplementation(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public Client createClient(Client client) {
        clientDAO.create(client);
        return client;
    }

    @Override
    public List<Client> getAllClients() {
        return clientDAO.getAll();
    }

    @Override
    public Client getClientById(Long id) {
        return clientDAO.getById(id)
                .orElseThrow(() -> new ClientDoesNotExistException(id));
    }

    @Override
    public Client updateClient(Long id, Client client) {
        clientDAO.update(id, client);
        return client;
    }

    @Override
    public void deleteClient(Long id) {
        clientDAO.delete(id);
    }
}
