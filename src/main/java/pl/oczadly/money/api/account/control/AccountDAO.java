package pl.oczadly.money.api.account.control;


import pl.oczadly.money.api.account.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDAO {

    List<Account> getAll();

    Optional<Account> getById(Long id);

    void update(Long id, Account account);

    void update(Account account1, Account account2);
}
