package pl.oczadly.money.api.account.boundary;

import pl.oczadly.money.api.account.entity.dto.AccountResponseDTO;
import pl.oczadly.money.api.account.entity.Account;

public class AccountMapperUtils {

    private AccountMapperUtils() {
    }

    public static AccountResponseDTO mapToAccountResponseDTO(Account persistedAccount) {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setId(persistedAccount.getId());
        accountResponseDTO.setBalance(persistedAccount.getBalance());
        accountResponseDTO.setClientId(persistedAccount.getClient().getId());

        return accountResponseDTO;
    }

}
