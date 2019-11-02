package pl.oczadly.money.api.account.boundary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pl.oczadly.money.api.account.control.AccountOperationService;
import pl.oczadly.money.api.account.control.AccountService;

import static spark.Spark.before;

public class AccountControllerTestInitializer {

    private AccountControllerTestInitializer() {
    }

    public static void startTestController() {
        before((req, res) -> res.type("application/json;charset=UTF-8"));

        AccountService accountService = new AccountServiceStub();
        AccountOperationService accountOperationService = new AccountOperationServiceStub();
        Gson gson = new GsonBuilder().create();

        AccountController accountController = new AccountController(accountService, accountOperationService, gson);
        accountController.setupEndpoints();
        accountController.setExceptionHandling();

    }
}
