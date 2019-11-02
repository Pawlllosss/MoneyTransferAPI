package pl.oczadly.money.api.client.boundary;

import pl.oczadly.money.api.client.control.ClientService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.before;

public class ClientControllerTestInitializer {

    private ClientControllerTestInitializer() {
    }

    public static void startTestController() {
        before((req, res) -> res.type("application/json;charset=UTF-8"));

        ClientService clientService = new ClientServiceStub();
        Gson gson = new GsonBuilder().create();

        ClientController clientController = new ClientController(clientService, gson);
        clientController.setupEndpoints();
        clientController.setExceptionHandling();
    }
}
