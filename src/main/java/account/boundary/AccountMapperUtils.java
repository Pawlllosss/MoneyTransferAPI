package account.boundary;

import account.entity.Account;
import account.entity.dto.AccountResponseDTO;

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
