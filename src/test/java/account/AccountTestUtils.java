package account;

import account.entity.Account;
import client.entity.Client;

import java.math.BigDecimal;

public class AccountTestUtils {

    private AccountTestUtils() {
    }

    public static Account createAccountEntity(BigDecimal balance, Client client) {
        Account account = new Account();
        account.setBalance(balance);
        account.setClient(client);

        return account;
    }
}
