package account.control;

import account.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDAO {

    List<Account> getAll();

    Optional<Account> getById(Long id);

    void update(Long id, Account account);

    void delete(Long id);
}
