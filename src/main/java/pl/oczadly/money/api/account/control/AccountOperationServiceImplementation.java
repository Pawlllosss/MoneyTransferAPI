package pl.oczadly.money.api.account.control;

import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.dto.AccountTransferDTO;
import pl.oczadly.money.api.account.entity.exception.AccountOperationNotSuccessfulException;
import pl.oczadly.money.api.account.entity.exception.AccountTransferNotSuccessfulException;
import pl.oczadly.money.api.account.entity.exception.IncorrectAccountOperationAmount;
import pl.oczadly.money.api.account.entity.exception.InsufficientFundsException;
import pl.oczadly.money.api.account.entity.exception.TransferToIdenticalAccountException;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static pl.oczadly.money.api.account.control.AccountUtils.convertDoubleToBigDecimal;

public class AccountOperationServiceImplementation implements AccountOperationService {

    private static final int LOCK_TIMEOUT_MS = 1000;

    private AccountDAO accountDAO;

    private AccountService accountService;

    @Inject
    public AccountOperationServiceImplementation(AccountDAO accountDAO, AccountService accountService) {
        this.accountDAO = accountDAO;
        this.accountService = accountService;
    }

    @Override
    public Account withdrawMoney(AccountOperationDTO accountOperationDTO) {
        BigDecimal amount = getOperationAmount(accountOperationDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Account account = mapAccountOperationDTOToAccount(accountOperationDTO);
        tryToWithdraw(amount, account);

        return account;
    }

    private BigDecimal getOperationAmount(AccountOperationDTO accountOperationDTO) {
        Double amountAsDouble = accountOperationDTO.getAmount();
        return convertDoubleToBigDecimal(amountAsDouble);
    }

    private boolean isAmountBiggerThanZero(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) >= 1;
    }

    private Account mapAccountOperationDTOToAccount(AccountOperationDTO accountOperationDTO) {
        Long id = accountOperationDTO.getId();
        return accountService.getAccountById(id);
    }

    private void tryToWithdraw(BigDecimal amount, Account account) {
        Lock accountLock = account.getLock();

        try {
            if (accountLock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                try {
                    withdrawMoney(amount, account);
                } finally {
                    accountLock.unlock();
                }
            } else {
                throw new AccountOperationNotSuccessfulException(account.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AccountOperationNotSuccessfulException(account.getId());
        }
    }

    private void withdrawMoney(BigDecimal amount, Account account) {
        Long accountId = account.getId();

        if (!isEnoughFundsToPerformWithdraw(account, amount)) {
            throw new InsufficientFundsException(accountId);
        }

        BigDecimal accountBalanceAfterWithdraw = getAccountBalanceAfterWithdrawal(account, amount);
        account.setBalance(accountBalanceAfterWithdraw);
        accountDAO.update(accountId, account);
    }

    private boolean isEnoughFundsToPerformWithdraw(Account account, BigDecimal amount) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.compareTo(amount) >= 0;
    }

    private BigDecimal getAccountBalanceAfterWithdrawal(Account account, BigDecimal amount) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.subtract(amount);
    }

    @Override
    public Account depositMoney(AccountOperationDTO accountOperationDTO) {
        BigDecimal amount = getOperationAmount(accountOperationDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Account account = mapAccountOperationDTOToAccount(accountOperationDTO);
        tryToDeposit(amount, account);

        return account;
    }

    private void tryToDeposit(BigDecimal amount, Account account) {
        Lock accountLock = account.getLock();

        try {
            if (accountLock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                try {
                    depositMoney(amount, account);
                } finally {
                    accountLock.unlock();
                }
            } else {
                throw new AccountOperationNotSuccessfulException(account.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AccountOperationNotSuccessfulException(account.getId());
        }
    }

    private void depositMoney(BigDecimal amount, Account account) {
        Long accountId = account.getId();

        BigDecimal accountBalanceAfterDeposit = getAccountBalanceAfterDeposit(account, amount);
        account.setBalance(accountBalanceAfterDeposit);
        accountDAO.update(accountId, account);
    }

    private BigDecimal getAccountBalanceAfterDeposit(Account account, BigDecimal amount) {
        BigDecimal accountBalance = account.getBalance();
        return accountBalance.add(amount);
    }

    @Override
    public Set<Account> transferMoney(AccountTransferDTO accountTransferDTO) {
        BigDecimal amount = getTransferAmount(accountTransferDTO);

        if (!isAmountBiggerThanZero(amount)) {
            throw new IncorrectAccountOperationAmount();
        }

        Long accountFromId = accountTransferDTO.getIdFrom();
        Long accountToId = accountTransferDTO.getIdTo();

        if (!doAccountIdsDiffer(accountFromId, accountToId)) {
            throw new TransferToIdenticalAccountException();
        }

        Account accountFrom = accountService.getAccountById(accountFromId);
        Account accountTo = accountService.getAccountById(accountToId);

        tryToTransfer(accountFrom, accountTo, amount);

        return Set.of(accountFrom, accountTo);
    }

    private BigDecimal getTransferAmount(AccountTransferDTO accountTransferDTO) {
        Double amountAsDouble = accountTransferDTO.getAmount();
        return convertDoubleToBigDecimal(amountAsDouble);
    }

    private boolean doAccountIdsDiffer(Long accountFromId, Long accountToId) {
        return !accountFromId.equals(accountToId);
    }

    private void tryToTransfer(Account accountFrom, Account accountTo, BigDecimal amount) {
        Lock accountFromLock = accountFrom.getLock();
        Lock accountToLock = accountTo.getLock();
        Long accountFromId = accountFrom.getId();
        Long accountToId = accountTo.getId();

        try {
            if (accountFromLock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                try {
                    try {
                        if (accountToLock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                            transferMoney(accountFrom, accountTo, amount);
                        } else {
                            throw new AccountTransferNotSuccessfulException(accountFromId, accountToId);
                        }
                    } finally {
                        accountToLock.unlock();
                    }
                } finally {
                    accountFromLock.unlock();
                }
            } else {
                throw new AccountTransferNotSuccessfulException(accountFromId, accountToId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AccountTransferNotSuccessfulException(accountFromId, accountToId);
        }
    }

    private void transferMoney(Account accountFrom, Account accountTo, BigDecimal amount) {
        if (!isEnoughFundsToPerformWithdraw(accountFrom, amount)) {
            throw new InsufficientFundsException(accountFrom.getId());
        }

        BigDecimal accountFromBalanceAfterTransfer = getAccountBalanceAfterWithdrawal(accountFrom, amount);
        accountFrom.setBalance(accountFromBalanceAfterTransfer);

        BigDecimal accountToBalanceAfterTransfer = getAccountBalanceAfterDeposit(accountTo, amount);
        accountTo.setBalance(accountToBalanceAfterTransfer);

        accountDAO.update(accountFrom, accountTo);
    }
}
