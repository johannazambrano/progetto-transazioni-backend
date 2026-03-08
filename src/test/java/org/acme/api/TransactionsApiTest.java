package org.acme.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionsApiTest {

    private static String createdId;

    @Test
    @Order(1)
    void testCreaTransaction() {
        String today = LocalDate.now().toString();

        createdId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "Spesa supermercato",
                        "amount": 45.50,
                        "category": {
                            "descrizione": "Alimentari",
                            "codice": "FOOD",
                            "budget": 300.0
                        },
                        "date": "%s"
                    }
                    """.formatted(today))
                .when()
                .post("/transactions/")
                .then()
                .statusCode(201)
                .header("Location", containsString("/transactions/"))
                .extract()
                .header("Location")
                .replaceAll(".*/transactions/", "");
    }

    @Test
    @Order(2)
    void testRicercaTransactions() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "paginazione": {
                            "numeroPagina": 0,
                            "numeroElementiPerPagina": 10
                        }
                    }
                    """)
                .when()
                .post("/transactions/ricerca")
                .then()
                .statusCode(200)
                .body("transactions", is(not(empty())))
                .body("paginazione", is(notNullValue()));
    }

    @Test
    @Order(3)
    void testRicercaConFiltroTitle() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "supermercato",
                        "paginazione": {
                            "numeroPagina": 0,
                            "numeroElementiPerPagina": 10
                        }
                    }
                    """)
                .when()
                .post("/transactions/ricerca")
                .then()
                .statusCode(200)
                .body("transactions", is(not(empty())));
    }

    @Test
    @Order(4)
    void testAggiornaTransaction() {
        String today = LocalDate.now().toString();

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "Spesa supermercato aggiornata",
                        "amount": 55.00,
                        "category": {
                            "descrizione": "Alimentari",
                            "codice": "FOOD",
                            "budget": 300.0
                        },
                        "date": "%s"
                    }
                    """.formatted(today))
                .when()
                .put("/transactions/" + createdId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void testDeleteTransaction() {
        given()
                .when()
                .delete("/transactions/" + createdId)
                .then()
                .statusCode(204);
    }

    @Test
    void testCreaTransactionSenzaCampiObbligatori() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "",
                        "amount": null
                    }
                    """)
                .when()
                .post("/transactions/")
                .then()
                .statusCode(400);
    }

    @Test
    void testRicercaSenzaPaginazione() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/transactions/ricerca")
                .then()
                .statusCode(anyOf(is(200), is(400)));
    }
}
