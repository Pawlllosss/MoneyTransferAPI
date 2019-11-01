package client.control;

import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ClientDAOImplementation implements ClientDAO {

    private EntityManager entityManager;

    @Inject
    public ClientDAOImplementation(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
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
    public void update(Long id, Client client) {
        getClientOrThrowException(id);
        client.setId(id);

        entityManager.getTransaction().begin();
        entityManager.merge(client);
        entityManager.getTransaction().commit();
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
