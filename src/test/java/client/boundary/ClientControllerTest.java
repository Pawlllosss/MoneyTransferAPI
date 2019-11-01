package client.boundary;

import client.ClientTestUtils;
import client.entity.Client;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

class ClientControllerTest {

    static final Long CLIENT_1_ID = 1L;
    static final String CLIENT_1_FIRST_NAME = "Paul";
    static final String CLIENT_1_SURNAME = "Smith";
    static final Long CLIENT_2_ID = 2L;
    static final String CLIENT_2_FIRST_NAME = "Rick";
    static final String CLIENT_2_SURNAME = "Sanchez";
    static final Long NOT_EXISTING_ID = 99999L;

    private static String CLIENT_PATH = "/client";
    private static Integer PORT = 4567;

    @BeforeAll
    static void init() {
        ClientControllerTestInitializer.startTestController();
        awaitInitialization();
    }

    @AfterAll
    static void tearDown() {
        stop();
    }

    @Test
    void shouldReturn201AndCreatedClientWhenTryingToCreateClient() {
        Client client = ClientTestUtils.createClient(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);
        client.setId(CLIENT_1_ID);

        given().body(client)
                .when()
                .port(PORT)
                .post(CLIENT_PATH)
                .then()
                .body("id", equalTo(Math.toIntExact(CLIENT_1_ID)))
                .body("firstName", equalTo(CLIENT_1_FIRST_NAME))
                .body("surname", equalTo(CLIENT_1_SURNAME))
                .statusCode(HttpStatus.CREATED_201);
    }

    @Test
    void shouldReturn200AndAllClientsWhenTryingToGetExistingClients() {
        given().when()
                .port(PORT)
                .get(CLIENT_PATH)
                .then()
                .body("size()", equalTo(2))
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    void shouldReturn200AndClientWhenTryingToGetExistingClient() {
        given().when()
                .port(PORT)
                .get(CLIENT_PATH + "/" + CLIENT_1_ID)
                .then()
                .body("id", equalTo(Math.toIntExact(CLIENT_1_ID)))
                .body("firstName", equalTo(CLIENT_1_FIRST_NAME))
                .body("surname", equalTo(CLIENT_1_SURNAME))
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    void shouldReturn404WhenTryingToGetNotExistingClient() {
        given().when()
                .port(PORT)
                .get(CLIENT_PATH + "/" + NOT_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void shouldReturn200AndUpdateClientWhenTryingToUpdateExistingClient() {
        Client client = ClientTestUtils.createClient(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);

        given().body(client)
                .when()
                .port(PORT)
                .put(CLIENT_PATH + "/" + CLIENT_1_ID)
                .then()
                .body("id", equalTo(Math.toIntExact(CLIENT_1_ID)))
                .body("firstName", equalTo(CLIENT_1_FIRST_NAME))
                .body("surname", equalTo(CLIENT_1_SURNAME))
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    void shouldReturn404WhenTryingToUpdateNotExistingClient() {
        Client client = ClientTestUtils.createClient(CLIENT_1_FIRST_NAME, CLIENT_1_SURNAME);

        given().body(client)
                .when()
                .port(PORT)
                .delete(CLIENT_PATH + "/" + NOT_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void shouldReturn204WhenTryingToDeleteExistingClient() {
        given().when()
                .port(PORT)
                .delete(CLIENT_PATH + "/" + CLIENT_1_ID)
                .then()
                .statusCode(HttpStatus.NO_CONTENT_204);
    }

    @Test
    void shouldReturn404WhenTryingToDeleteNotExistingClient() {
        given().when()
                .port(PORT)
                .delete(CLIENT_PATH + "/" + NOT_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }
}