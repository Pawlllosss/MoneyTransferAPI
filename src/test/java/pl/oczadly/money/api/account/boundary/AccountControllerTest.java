package pl.oczadly.money.api.account.boundary;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.oczadly.money.api.account.entity.dto.AccountCreateDTO;
import pl.oczadly.money.api.account.entity.dto.AccountOperationDTO;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static pl.oczadly.money.api.account.AccountTestUtils.createAccountCreateDTO;
import static pl.oczadly.money.api.account.AccountTestUtils.createAccountOperationDTO;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

class AccountControllerTest {

    static final BigDecimal ACCOUNT_1_BALANCE = new BigDecimal("100.5");
    static final BigDecimal ACCOUNT_2_BALANCE = new BigDecimal("200.5");
    static final Long ACCOUNT_1_ID = 1L;
    static final Long ACCOUNT_2_ID = 2L;
    static final Long NOT_EXISTING_ID = 111L;
    static final String CLIENT_FIRST_NAME = "Paul";
    static final String CLIENT_SURNAME = "Smith";
    static final Long CLIENT_ID = 1L;

    private static String ACCOUNT_PATH = "/account";
    private static final String WITHDRAW_PATH = "/withdraw";
    private static final String DEPOSIT_PATH = "/deposit";
    private static final String TRANSFER_PATH = "/transfer";
    private static Integer PORT = 4567;

    @BeforeAll
    static void init() {
        AccountControllerTestInitializer.startTestController();
        awaitInitialization();
    }

    @AfterAll
    static void tearDown() {
        stop();
    }

    @Test
    void shouldReturn201AndCreatedAccountWhenTryingToCreateAccount() {
        AccountCreateDTO accountCreateDTO = createAccountCreateDTO(ACCOUNT_1_BALANCE.doubleValue(), CLIENT_ID);

        given().body(accountCreateDTO)
                .when()
                .port(PORT)
                .post(ACCOUNT_PATH)
                .then()
                .body("id", equalTo(Math.toIntExact(ACCOUNT_1_ID)))
                .body("clientId", equalTo(Math.toIntExact(CLIENT_ID)))
                .statusCode(HttpStatus.CREATED_201);
    }

    @Test
    void shouldReturn200AndAllAccountsWhenTryingToGetExistingAccounts() {
        given().when()
                .port(PORT)
                .get(ACCOUNT_PATH)
                .then()
                .body("size()", equalTo(2))
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    void shouldReturn200AndAccountWhenTryingToGetExistingAccount() {
        given().when()
                .port(PORT)
                .get(ACCOUNT_PATH + "/" + ACCOUNT_1_ID)
                .then()
                .body("id", equalTo(Math.toIntExact(ACCOUNT_1_ID)))
                .body("clientId", equalTo(Math.toIntExact(CLIENT_ID)))
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    void shouldReturn404WhenTryingToGetNotExistingAccount() {
        given().when()
                .port(PORT)
                .get(ACCOUNT_PATH + "/" + NOT_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void shouldReturn400WhenTryingToWithdrawZeroMoney() {
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, 0d);

        given().body(accountOperationDTO)
                .when()
                .port(PORT)
                .post(ACCOUNT_PATH + WITHDRAW_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void shouldReturn400WhenNotEnoughFundsToPerformWithdraw() {
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, ACCOUNT_1_BALANCE.doubleValue());

        given().body(accountOperationDTO)
                .when()
                .port(PORT)
                .post(ACCOUNT_PATH + WITHDRAW_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void shouldReturn400WhenTryingToDepositZeroMoney() {
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, 0d);

        given().body(accountOperationDTO)
                .when()
                .port(PORT)
                .post(ACCOUNT_PATH + DEPOSIT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void shouldReturn400WhenTryingToTransferZeroMoney() {
        AccountOperationDTO accountOperationDTO = createAccountOperationDTO(ACCOUNT_1_ID, 0d);

        given().body(accountOperationDTO)
                .when()
                .port(PORT)
                .post(ACCOUNT_PATH + TRANSFER_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void shouldReturn204WhenTryingToDeleteExistingAccount() {
        given().when()
                .port(PORT)
                .delete(ACCOUNT_PATH + "/" + ACCOUNT_1_ID)
                .then()
                .statusCode(HttpStatus.NO_CONTENT_204);
    }

    @Test
    void shouldReturn404WhenTryingToDeleteNotExistingClient() {
        given().when()
                .port(PORT)
                .delete(ACCOUNT_PATH + "/" + NOT_EXISTING_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }
}