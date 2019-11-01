package client.boundary;

import account.boundary.AccountMapperUtils;
import account.entity.Account;
import account.entity.dto.AccountResponseDTO;
import api.controller.RestControllerWithExceptionHandling;
import api.dto.ExceptionDTO;
import client.control.ClientService;
import client.entity.Client;
import client.entity.dto.ClientResponseDTO;
import client.entity.exception.ClientDoesNotExistException;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.ExceptionHandler;
import spark.Request;
import spark.Route;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static api.controller.ControllerUtils.mapToExceptionDTO;
import static api.controller.ControllerUtils.parseIdFromNamedQueryParams;
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
            ClientResponseDTO clientResponseDTO = mapClientToClientResponseDTO(client);
            return gson.toJson(clientResponseDTO);
        };
    }

    private ClientResponseDTO mapClientToClientResponseDTO(Client client) {
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO();
        clientResponseDTO.setId(client.getId());
        clientResponseDTO.setFirstName(client.getFirstName());
        clientResponseDTO.setSurname(client.getSurname());

        Set<AccountResponseDTO> accountResponseDTOs = mapClientAccountsToAccountResponseDTOs(client);
        clientResponseDTO.setAccounts(accountResponseDTOs);

        return clientResponseDTO;
    }

    private Set<AccountResponseDTO> mapClientAccountsToAccountResponseDTOs(Client client) {
        Set<Account> accounts = client.getAccounts();
        return  accounts.stream()
                .map(AccountMapperUtils::mapToAccountResponseDTO)
                .collect(Collectors.toSet());
    }

    private Client parseClientFromRequestBody(Request request) {
        String requestBody = request.body();
        return gson.fromJson(requestBody, Client.class);
    }

    private Route setupGetAllClientsEndpoint() {
        return (req, res) -> {
            List<Client> clients = clientService.getAllClients();
            List<ClientResponseDTO> clientResponseDTOs = clients.stream()
                    .map(this::mapClientToClientResponseDTO)
                    .collect(Collectors.toList());

            return gson.toJson(clientResponseDTOs);
        };
    }

    private Route setupGetClientByIdEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            Client client = clientService.getClientById(id);
            ClientResponseDTO clientResponseDTO = mapClientToClientResponseDTO(client);

            return gson.toJson(clientResponseDTO);
        };
    }

    private Route setupUpdateClientEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            Client client = parseClientFromRequestBody(req);
            Client updatedClient = clientService.updateClient(id, client);
            ClientResponseDTO clientResponseDTO = mapClientToClientResponseDTO(client);

            return gson.toJson(clientResponseDTO);
        };
    }

    private Route setupDeleteClientEndpoint() {
        return (req, res) -> {
            Long id = parseIdFromNamedQueryParams(req);
            clientService.deleteClient(id);

            res.status(HttpStatus.NO_CONTENT_204);
            return "";
        };

    }

    @Override
    public void setExceptionHandling() {
        exception(ClientDoesNotExistException.class, setupClientDoesNotExistExceptionMapping());
    }

    private ExceptionHandler<ClientDoesNotExistException> setupClientDoesNotExistExceptionMapping() {
        return (ex, req, res) -> {
            ExceptionDTO exceptionDTO = mapToExceptionDTO(ex);

            res.status(HttpStatus.NOT_FOUND_404);
            res.body(gson.toJson(exceptionDTO));
        };
    }
}
