package pl.oczadly.money.api.account;

import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.client.entity.Client;

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

    public static AccountCreateDTO createAccountCreateDTO(Double balance, Long clientId) {
        AccountCreateDTO accountCreateDTO = new AccountCreateDTO();
        accountCreateDTO.setBalance(balance);
        accountCreateDTO.setClientId(clientId);

        return accountCreateDTO;
    }

}
