package pl.oczadly.money.api.api.configuration;

import pl.oczadly.money.api.account.boundary.AccountController;
import pl.oczadly.money.api.client.boundary.ClientController;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.inject.Inject;

import static spark.Spark.before;

public class ApiInitializer {

    private ClientController clientController;

    private AccountController accountController;

    public static void startApplication() {
        Injector injector = Guice.createInjector(new ApiModule());
        injector.getInstance(ApiInitializer.class).run();
    }

    @Inject
    public ApiInitializer(ClientController clientController, AccountController accountController) {
        this.clientController = clientController;
        this.accountController = accountController;
    }

    public void run() {
        setJsonContentTypeHeaderToResponse();

        clientController.setupEndpoints();
        clientController.setExceptionHandling();

        accountController.setupEndpoints();
        accountController.setExceptionHandling();
    }

    private void setJsonContentTypeHeaderToResponse() {
        before((req, res) -> res.type("application/json;charset=UTF-8"));
    }
}
