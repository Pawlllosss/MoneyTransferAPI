package client.boundary;

import client.control.ClientService;
import client.entity.Client;
import client.entity.exception.ClientDoesNotExistException;
import com.google.gson.Gson;
import controller.RestControllerWithExceptionHandling;
import org.eclipse.jetty.http.HttpStatus;
import spark.ExceptionHandler;
import spark.Request;
import spark.Route;

import javax.inject.Inject;

import static controller.ControllerUtils.parseIdFromNamedQueryParams;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class ClientController implements RestControllerWithExceptionHandling {

    private static String CLIENT_PATH = "/client";

    private ClientService clientService;
    private Gson gson;

    @Inject
    public ClientController(ClientService clientService, Gson gson) {
        this.clientService = clientService;
        this.gson = gson;
    }

    @Override
    public void setupEndpoints() {
        post(CLIENT_PATH, setupCreateClientEndpoint());
        get(CLIENT_PATH, setupGetAllClientsEndpoint());
        get(CLIENT_PATH + "/:id", setupGetClientByIdEndpoint());
        put(CLIENT_PATH + "/:id", setupUpdateClientEndpoint());
        delete(CLIENT_PATH + "/:id", setupDeleteClientEndpoint());
    }

    private Route setupCreateClientEndpoint() {
        return (req, res) -> {
            Client client = parseClientFromRequestBody(req);
            Client persistedClient = clientService.createClient(client);

            res.status(HttpStatus.CREATED_201);
            return gson.toJson(persistedClient);
        };
    }

    private Client parseClientFromRequestBody(Request request) {
        String requestBody = request.body();
        return gson.fromJson(requestBody, Client.class);
    }

    private Route setupGetAllClientsEndpoint() {
        return (req, res) -> gson.toJson(clientService.getAllClients());
    }

    private Route setupGetClientByIdEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            return gson.toJson(clientService.getClientById(id));
        };
    }

    private Route setupUpdateClientEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            Client client = parseClientFromRequestBody(req);
            Client updatedClient = clientService.updateClient(id, client);

            res.status(HttpStatus.NO_CONTENT_204);
            return gson.toJson(updatedClient);
        };
    }

    private Route setupDeleteClientEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            clientService.deleteClient(id);

            res.status(HttpStatus.ACCEPTED_202);
            return "";
        };

    }

    @Override
    public void setExceptionHandling() {
        exception(ClientDoesNotExistException.class, setupClientDoesNotExistExceptionMapping());
    }

    private ExceptionHandler<ClientDoesNotExistException> setupClientDoesNotExistExceptionMapping() {
        return (ex, req, res) -> {
            res.status(HttpStatus.NOT_FOUND_404);
            res.body(gson.toJson(ex.getMessage()));
        };
    }
}
