package pl.oczadly.money.api.account.entity.exception;

public class TransferToIdenticalAccountException extends RuntimeException {

    public TransferToIdenticalAccountException() {
        super("Trying to transfer money to the same account");
    }
}
