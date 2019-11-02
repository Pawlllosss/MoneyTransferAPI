package account.entity.exception;

public class AccountOperationNotSuccessfulException extends IllegalStateException {

    public AccountOperationNotSuccessfulException(Long id) {
        super("Operation on account with following id not successful: " + id);
    }
}
