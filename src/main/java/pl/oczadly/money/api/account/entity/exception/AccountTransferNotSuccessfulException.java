package pl.oczadly.money.api.account.entity.exception;

public class AccountTransferNotSuccessfulException extends IllegalStateException {

    public AccountTransferNotSuccessfulException(Long id1, Long id2) {
        super("Transfer between accounts with following ids not successful: " + id1 + ", " + id2);
    }
}
