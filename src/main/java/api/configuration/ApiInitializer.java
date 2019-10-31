package api.configuration;

import client.boundary.ClientController;

import javax.inject.Inject;

import static spark.Spark.before;

public class ApiInitializer {

    private ClientController clientController;

    @Inject
    public ApiInitializer(ClientController clientController) {
        this.clientController = clientController;
    }

    public void run() {
        setJsonContentTypeHeaderToResponse();

        clientController.setupEndpoints();
        clientController.setExceptionHandling();
    }

    private void setJsonContentTypeHeaderToResponse() {
        before((req, res) -> res.type("application/json;charset=UTF-8"));
    }
}
