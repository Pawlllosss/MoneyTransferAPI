package pl.oczadly.money.api.account.boundary;

import pl.oczadly.money.api.account.AccountTestUtils;
import pl.oczadly.money.api.account.control.AccountService;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;
import pl.oczadly.money.api.client.ClientTestUtils;
import pl.oczadly.money.api.client.entity.Client;

import java.util.List;

import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_1_BALANCE;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_1_ID;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_2_BALANCE;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.ACCOUNT_2_ID;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.CLIENT_FIRST_NAME;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.CLIENT_ID;
import static pl.oczadly.money.api.account.boundary.AccountControllerTest.CLIENT_SURNAME;

public class AccountServiceStub implements AccountService {

    @Override
    public Account createAccount(AccountCreateDTO accountCreateDTO) {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);

        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account1 = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account1.setId(ACCOUNT_1_ID);
        Account account2 = AccountTestUtils.createAccountEntity(ACCOUNT_2_BALANCE, client);
        account2.setId(ACCOUNT_2_ID);

        return List.of(account1, account2);
    }

    @Override
    public Account getAccountById(Long id) {
        if (!id.equals(ACCOUNT_1_ID)) {
            throw new AccountDoesNotExistException(id);
        }

        Client client = ClientTestUtils.createClientEntity(CLIENT_FIRST_NAME, CLIENT_SURNAME);
        client.setId(CLIENT_ID);
        Account account = AccountTestUtils.createAccountEntity(ACCOUNT_1_BALANCE, client);
        account.setId(ACCOUNT_1_ID);

        return account;
    }

    @Override
    public void deleteAccount(Long id) {
        if (!id.equals(ACCOUNT_1_ID)) {
            throw new AccountDoesNotExistException(id);
        }
    }
}
