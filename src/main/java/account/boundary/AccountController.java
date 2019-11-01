package account.boundary;

import account.control.AccountService;
import account.entity.Account;
import account.entity.dto.AccountCreateDTO;
import account.entity.dto.AccountOperationDTO;
import account.entity.dto.AccountResponseDTO;
import account.entity.exception.AccountDoesNotExistException;
import account.entity.exception.IncorrectAccountOperationAmount;
import account.entity.exception.InsufficientFundsException;
import api.controller.RestControllerWithExceptionHandling;
import api.dto.ExceptionDTO;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.ExceptionHandler;
import spark.Request;
import spark.Route;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static account.boundary.AccountMapperUtils.mapToAccountResponseDTO;
import static api.controller.ControllerUtils.mapToExceptionDTO;
import static api.controller.ControllerUtils.parseIdFromNamedQueryParams;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController implements RestControllerWithExceptionHandling {

    private static String ACCOUNT_PATH = "/account";

    private AccountService accountService;
    private Gson gson;

    @Inject
    public AccountController(AccountService accountService, Gson gson) {
        this.accountService = accountService;
        this.gson = gson;
    }

    @Override
    public void setupEndpoints() {
        post(ACCOUNT_PATH, setupCreateAccountEndpoint());
        get(ACCOUNT_PATH, setupGetAllAccountsEndpoint());
        get(ACCOUNT_PATH + "/:id", setupGetAccountByIdEndpoint());
        post(ACCOUNT_PATH + "/withdraw", setupWithdrawMoneyEndpoint());
        post(ACCOUNT_PATH + "/deposit", setupDepositMoneyEndpoint());
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
            Account account = accountService.withdrawMoney(accountOperationDTO);

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
            Account account = accountService.depositMoney(accountOperationDTO);

            AccountResponseDTO accountResponseDTO = mapToAccountResponseDTO(account);
            return gson.toJson(accountResponseDTO);
        };
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
        exception(IncorrectAccountOperationAmount.class, setupIncorrectAccountOperationAmountExceptionMapping());
        exception(InsufficientFundsException.class, setupInsufficientFundsExceptionMapping());
    }

    private ExceptionHandler<AccountDoesNotExistException> setupAccountDoesNotExistExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.NOT_FOUND_404);
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
}
