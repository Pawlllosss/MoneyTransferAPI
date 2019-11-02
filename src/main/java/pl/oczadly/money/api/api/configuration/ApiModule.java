package pl.oczadly.money.api.api.configuration;

import pl.oczadly.money.api.account.control.AccountDAO;
import pl.oczadly.money.api.account.control.AccountDAOImplementation;
import pl.oczadly.money.api.account.control.AccountOperationService;
import pl.oczadly.money.api.account.control.AccountOperationServiceImplementation;
import pl.oczadly.money.api.account.control.AccountService;
import pl.oczadly.money.api.account.control.AccountServiceImplementation;
import pl.oczadly.money.api.client.control.ClientDAO;
import pl.oczadly.money.api.client.control.ClientDAOImplementation;
import pl.oczadly.money.api.client.control.ClientService;
import pl.oczadly.money.api.client.control.ClientServiceImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ApiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ClientDAO.class).to(ClientDAOImplementation.class);
        bind(ClientService.class).to(ClientServiceImplementation.class);
        bind(AccountDAO.class).to(AccountDAOImplementation.class);
        bind(AccountService.class).to(AccountServiceImplementation.class);
        bind(AccountOperationService.class).to(AccountOperationServiceImplementation.class);

        Gson gson = new GsonBuilder().create();
        bind(Gson.class).toInstance(gson);

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("money_transfer");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        bind(EntityManager.class).toInstance(entityManager);
    }
}
