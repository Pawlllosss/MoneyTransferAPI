package pl.oczadly.money.api.account.entity.exception;

public class IncorrectAccountOperationAmount extends IllegalArgumentException {

    public IncorrectAccountOperationAmount() {
        super("Amount should be bigger than zero");
    }
}
