package pl.oczadly.money.api.client.control;

import pl.oczadly.money.api.client.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {

    void create(Client client);

    List<Client> getAll();

    Optional<Client> getById(Long id);

    Client update(Long id, Client client);

    void delete(Long id);
}
