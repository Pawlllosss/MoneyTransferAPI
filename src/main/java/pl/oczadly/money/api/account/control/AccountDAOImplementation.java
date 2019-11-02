package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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
    @Transactional
    public void update(Long id, Account account) {
        getAccountOrThrowException(id);
        account.setId(id);

        entityManager.merge(account);
    }

    private Account getAccountOrThrowException(Long id) {
        return getById(id).orElseThrow(() -> new AccountDoesNotExistException(id));
    }

    @Override
    @Transactional
    public void update(Account account1, Account account2) {
        entityManager.merge(account1);
        entityManager.merge(account2);
    }
}
