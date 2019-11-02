package pl.oczadly.money.api.account.entity.exception;

public class InsufficientFundsException extends IllegalStateException {

    public InsufficientFundsException(Long id) {
        super("There is not enough money to perform operation on account with id: " + id);
    }
}
