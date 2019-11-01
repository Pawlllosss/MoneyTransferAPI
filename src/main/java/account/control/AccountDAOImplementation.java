package account.control;

import account.entity.Account;

import java.util.List;
import java.util.Optional;

public class AccountDAOImplementation implements AccountDAO {

    @Override
    public List<Account> getAll() {
        return null;
    }

    @Override
    public Optional<Account> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public void update(Long id, Account account) {

    }

    @Override
    public void delete(Long id) {

    }
}
