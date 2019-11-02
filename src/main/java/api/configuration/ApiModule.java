package api.configuration;

import account.control.AccountDAO;
import account.control.AccountDAOImplementation;
import account.control.AccountService;
import account.control.AccountServiceImplementation;
import client.control.ClientDAO;
import client.control.ClientDAOImplementation;
import client.control.ClientService;
import client.control.ClientServiceImplementation;
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

        Gson gson = new GsonBuilder().create();
        bind(Gson.class).toInstance(gson);

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("money_transfer");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        bind(EntityManager.class).toInstance(entityManager);
    }
}
