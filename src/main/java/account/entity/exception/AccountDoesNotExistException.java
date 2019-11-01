package account.entity.exception;

public class AccountDoesNotExistException extends RuntimeException {

    public AccountDoesNotExistException(Long id) {
        super("Account with the following id does not exists: " + id);
    }
}
