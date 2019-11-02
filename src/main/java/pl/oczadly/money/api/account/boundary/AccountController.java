package pl.oczadly.money.api.account.boundary;

import pl.oczadly.money.api.account.control.AccountOperationService;
import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;
import pl.oczadly.money.api.account.entity.dto.AccountResponseDTO;
import pl.oczadly.money.api.account.entity.dto.AccountTransferDTO;
import pl.oczadly.money.api.account.entity.exception.AccountDoesNotExistException;
import pl.oczadly.money.api.account.entity.exception.AccountOperationNotSuccessfulException;
import pl.oczadly.money.api.account.entity.exception.AccountTransferNotSuccessfulException;
import pl.oczadly.money.api.account.entity.exception.IncorrectAccountOperationAmount;
import pl.oczadly.money.api.account.entity.exception.InsufficientFundsException;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import pl.oczadly.money.api.account.control.AccountService;
import pl.oczadly.money.api.account.entity.Account;
import pl.oczadly.money.api.account.entity.exception.TransferToIdenticalAccountException;
import pl.oczadly.money.api.api.controller.RestControllerWithExceptionHandling;
import pl.oczadly.money.api.api.dto.ExceptionDTO;
import spark.ExceptionHandler;
import spark.Request;
import spark.Route;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.oczadly.money.api.account.boundary.AccountMapperUtils.mapToAccountResponseDTO;
import static pl.oczadly.money.api.api.controller.ControllerUtils.mapToExceptionDTO;
import static pl.oczadly.money.api.api.controller.ControllerUtils.parseIdFromNamedQueryParams;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController implements RestControllerWithExceptionHandling {

    private static String ACCOUNT_PATH = "/account";

    private AccountService accountService;
    private AccountOperationService accountOperationService;
    private Gson gson;

    @Inject
    public AccountController(AccountService accountService, AccountOperationService accountOperationService, Gson gson) {
        this.accountService = accountService;
        this.accountOperationService = accountOperationService;
        this.gson = gson;
    }

    @Override
    public void setupEndpoints() {
        post(ACCOUNT_PATH, setupCreateAccountEndpoint());
        get(ACCOUNT_PATH, setupGetAllAccountsEndpoint());
        get(ACCOUNT_PATH + "/:id", setupGetAccountByIdEndpoint());
        post(ACCOUNT_PATH + "/withdraw", setupWithdrawMoneyEndpoint());
        post(ACCOUNT_PATH + "/deposit", setupDepositMoneyEndpoint());
        post(ACCOUNT_PATH + "/transfer", setupTransferMoneyEndpoint());
        delete(ACCOUNT_PATH + "/:id", setupDeleteAccountEndpoint());
    }

    private Route setupCreateAccountEndpoint() {
        return (req, res) -> {
            AccountCreateDTO accountCreateDTO = parseAccountCreateDTOFromRequestBody(req);
            Account persistedAccount = accountService.createAccount(accountCreateDTO);

            res.status(HttpStatus.CREATED_201);
            AccountResponseDTO accountResponseDTO = mapToAccountResponseDTO(persistedAccount);
            return gson.toJson(accountResponseDTO);
        };
    }

    private AccountCreateDTO parseAccountCreateDTOFromRequestBody(Request req) {
        String requestBody = req.body();
        return gson.fromJson(requestBody, AccountCreateDTO.class);
    }

    private Route setupGetAllAccountsEndpoint() {
        return (req, res) -> {
            List<Account> allAccounts = accountService.getAllAccounts();

            List<AccountResponseDTO> accountResponseDTO = allAccounts.stream()
                    .map(AccountMapperUtils::mapToAccountResponseDTO)
                    .collect(Collectors.toList());
            return gson.toJson(accountResponseDTO);
        };
    }

    private Route setupGetAccountByIdEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            Account account = accountService.getAccountById(id);

            AccountResponseDTO accountResponseDTO = mapToAccountResponseDTO(account);
            return gson.toJson(accountResponseDTO);
        };
    }

    private Route setupWithdrawMoneyEndpoint() {
        return (req, res) -> {
            AccountOperationDTO accountOperationDTO = parseAccountOperationDTOFromRequestBody(req);
            Account account = accountOperationService.withdrawMoney(accountOperationDTO);

            AccountResponseDTO accountResponseDTO = mapToAccountResponseDTO(account);
            return gson.toJson(accountResponseDTO);
        };

    }

    private AccountOperationDTO parseAccountOperationDTOFromRequestBody(Request req) {
        String requestBody = req.body();
        return gson.fromJson(requestBody, AccountOperationDTO.class);
    }

    private Route setupDepositMoneyEndpoint() {
        return (req, res) -> {
            AccountOperationDTO accountOperationDTO = parseAccountOperationDTOFromRequestBody(req);
            Account account = accountOperationService.depositMoney(accountOperationDTO);

            AccountResponseDTO accountResponseDTO = mapToAccountResponseDTO(account);
            return gson.toJson(accountResponseDTO);
        };
    }

    private Route setupTransferMoneyEndpoint() {
        return (req, res) -> {
            AccountTransferDTO accountTransferDTO = parseAccountTransferDTOFromRequestBody(req);
            Set<Account> accounts = accountOperationService.transferMoney(accountTransferDTO);

            List<AccountResponseDTO> accountResponseDTO = accounts.stream()
                    .map(AccountMapperUtils::mapToAccountResponseDTO)
                    .collect(Collectors.toList());
            return gson.toJson(accountResponseDTO);
        };
    }

    private AccountTransferDTO parseAccountTransferDTOFromRequestBody(Request req) {
        String requestBody = req.body();
        return gson.fromJson(requestBody, AccountTransferDTO.class);
    }

    private Route setupDeleteAccountEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            accountService.deleteAccount(id);

            res.status(HttpStatus.NO_CONTENT_204);
            return "";
        };
    }

    @Override
    public void setExceptionHandling() {
        exception(AccountDoesNotExistException.class, setupAccountDoesNotExistExceptionMapping());
        exception(AccountOperationNotSuccessfulException.class, setupAccountOperationNotSuccessfulExceptionMapping());
        exception(AccountTransferNotSuccessfulException.class, setupAccountTransferNotSuccessfulExceptionMapping());
        exception(IncorrectAccountOperationAmount.class, setupIncorrectAccountOperationAmountExceptionMapping());
        exception(InsufficientFundsException.class, setupInsufficientFundsExceptionMapping());
        exception(TransferToIdenticalAccountException.class, setupTransferToIdenticalAccountExceptionMapping());
    }

    private ExceptionHandler<AccountDoesNotExistException> setupAccountDoesNotExistExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.NOT_FOUND_404);
            res.body(gson.toJson(exceptionDTO));
        };
    }

    private ExceptionHandler<AccountOperationNotSuccessfulException> setupAccountOperationNotSuccessfulExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(gson.toJson(exceptionDTO));
        };
    }

    private ExceptionHandler<AccountTransferNotSuccessfulException> setupAccountTransferNotSuccessfulExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(gson.toJson(exceptionDTO));
        };
    }

    private ExceptionHandler<IncorrectAccountOperationAmount> setupIncorrectAccountOperationAmountExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(gson.toJson(exceptionDTO));
        };
    }

    private ExceptionHandler<InsufficientFundsException> setupInsufficientFundsExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(gson.toJson(exceptionDTO));
        };
    }

    private ExceptionHandler<TransferToIdenticalAccountException> setupTransferToIdenticalAccountExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(gson.toJson(exceptionDTO));
        };
    }
}
