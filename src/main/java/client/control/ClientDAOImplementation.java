package client.control;

import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public class ClientDAOImplementation implements ClientDAO {

    private EntityManager entityManager;

    @Inject
    public ClientDAOImplementation(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void create(Client client) {
        entityManager.getTransaction().begin();
        entityManager.persist(client);
        entityManager.getTransaction().commit();
    }

    @Override
    public List<Client> getAll() {
        return entityManager.createQuery("from client")
                .getResultList();
    }

    @Override
    public Optional<Client> getById(Long id) {
        Client foundClient = entityManager.find(Client.class, id);
        return Optional.ofNullable(foundClient);
    }

    @Override
    @Transactional
    public Client update(Long id, Client client) {
        Client clientToModify = getClientOrThrowException(id);
        clientToModify.setFirstName(client.getFirstName());
        clientToModify.setSurname(client.getSurname());

        entityManager.getTransaction().begin();
        entityManager.merge(clientToModify);
        entityManager.getTransaction().commit();

        return clientToModify;
    }

    private Client getClientOrThrowException(Long id) {
        return getById(id).orElseThrow(() -> new ClientDoesNotExistException(id));
    }


    @Override
    public void delete(Long id) {
        Client client = getClientOrThrowException(id);

        entityManager.getTransaction().begin();
        entityManager.remove(client);
        entityManager.getTransaction().commit();
    }
}
