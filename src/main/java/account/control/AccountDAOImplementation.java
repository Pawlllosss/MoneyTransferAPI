package account.control;

import account.entity.Account;
import account.entity.exception.AccountDoesNotExistException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class AccountDAOImplementation implements AccountDAO {

    private EntityManager entityManager;

    @Inject
    public AccountDAOImplementation(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Account> getAll() {
        return entityManager.createQuery("from account")
                .getResultList();
    }

    @Override
    public Optional<Account> getById(Long id) {
        Account foundAccount = entityManager.find(Account.class, id);
        return Optional.ofNullable(foundAccount);
    }

    @Override
    public void update(Long id, Account account) {
        getAccountOrThrowException(id);
        account.setId(id);

        entityManager.getTransaction().begin();
        entityManager.merge(account);
        entityManager.getTransaction().commit();
    }

    private Account getAccountOrThrowException(Long id) {
        return getById(id).orElseThrow(() -> new AccountDoesNotExistException(id));
    }
}
