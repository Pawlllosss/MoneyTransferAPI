package pl.oczadly.money.api.account.control;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.oczadly.money.api.account.AccountTestUtils;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.client.ClientTestUtils;
import pl.oczadly.money.api.client.entity.Client;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AccountDAOImplementationIntegrationTest {

    private static final BigDecimal ACCOUNT_1_BALANCE = new BigDecimal("100.5");
    private static final BigDecimal ACCOUNT_2_BALANCE = new BigDecimal("200.5");
    private static final String CLIENT_1_FIRST_NAME = "Paul";
    private static final String CLIENT_1_SURNAME = "Smith";

    private AccountDAOImplementation accountDAO;
    private EntityManager entityManager;
    private List<Account> createdAccounts;
    private Client createdClient;

    public AccountDAOImplementationIntegrationTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("money_transfer");
        entityManager = entityManagerFactory.createEntityManager();
        accountDAO = new AccountDAOImplementation(entityManager);
    }

    @BeforeEach
    void setUp() {
        createdAccounts = new LinkedList<>();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("money_transfer");
        entityManager = entityManagerFactory.createEntityManager();
    }

    private void persistClientWithoutAccountsInDatabase() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);

        entityManager.getTransaction().begin();
        entityManager.persist(client);
        entityManager.getTransaction().commit();

        createdClient = client;
    }

    private void persistClientWithAccountsInDatabase() {
        Account account1 = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, createdClient);
        Account account2 = AccountTestUtils.createAccountEntity(ACCOUNT_2_BALANCE, createdClient);

        createdAccounts.add(account1);
        createdAccounts.add(account2);

        Client client = ClientTestUtils.createClientEntity(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);
        client.addAccount(account1);
        client.addAccount(account2);

        entityManager.getTransaction().begin();
        entityManager.persist(client);
        entityManager.getTransaction().commit();

        createdClient = client;
    }

    @AfterEach
    void clearDatabase() {
        entityManager.getTransaction().begin();
        createdAccounts.stream()
                .map(Account::getId)
                .map(id -> entityManager.find(Account.class, id))
                .forEach(account -> entityManager.remove(account));

        Client clientFromEntityManager = entityManager.find(Client.class, createdClient.getId());
        entityManager.remove(clientFromEntityManager);
        entityManager.getTransaction().commit();

        createdAccounts.clear();
    }

    @Test
    void shouldReturnNoAccountsWhenNoAccountsExist() {
        persistClientWithoutAccountsInDatabase();
        List<Account> retrievedAccounts = accountDAO.getAll();
        assertThat(retrievedAccounts).isEmpty();
    }

    @Test
    void shouldReturnAllExistingAccountsWhenAccountExists() {
        persistClientWithAccountsInDatabase();
        List<Account> retrievedAccounts = accountDAO.getAll();

        assertThat(retrievedAccounts).hasSize(2);
    }

    @Test
    void shouldReturnUpdatedAccountWhenAccountUpdated() {
        persistClientWithAccountsInDatabase();

        Account accountToUpdate = createdAccounts.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No account exist"));
        Long accountId = accountToUpdate.getId();
        BigDecimal updatedBalance = BigDecimal.ONE;
        Account accountWithUpdatedInformation = new Account();
        accountWithUpdatedInformation.setBalance(updatedBalance);
        accountWithUpdatedInformation.setClient(createdClient);
        accountDAO.update(accountId, accountWithUpdatedInformation);

        Optional<Account> retrievedAccount = accountDAO.getById(accountId);
        assertThat(retrievedAccount).isNotEmpty();
        BigDecimal retrievedAccountBalance = retrievedAccount.get().getBalance();
        assertThat(retrievedAccountBalance).isEqualTo(updatedBalance);
    }
}