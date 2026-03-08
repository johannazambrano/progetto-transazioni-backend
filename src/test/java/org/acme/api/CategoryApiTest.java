package org.acme.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryApiTest {

    private static String createdId;

    @Test
    @Order(1)
    void testCreaCategory() {
        createdId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "descrizione": "Test Category",
                        "codice": "TEST_CAT",
                        "budget": 100.0,
                        "colore": "#FF0000"
                    }
                    """)
                .when()
                .post("/categories/")
                .then()
                .statusCode(201)
                .header("Location", containsString("/categories/"))
                .extract()
                .header("Location")
                .replaceAll(".*/categories/", "");
    }

    @Test
    @Order(2)
    void testElencoCategories() {
        given()
                .when()
                .get("/categories/")
                .then()
                .statusCode(200)
                .body("categories", is(not(empty())));
    }

    @Test
    @Order(3)
    void testAggiornaCategory() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "descrizione": "Test Category Updated",
                        "codice": "TEST_CAT",
                        "budget": 200.0,
                        "colore": "#00FF00"
                    }
                    """)
                .when()
                .put("/categories/" + createdId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    void testDeleteCategory() {
        given()
                .when()
                .delete("/categories/" + createdId)
                .then()
                .statusCode(204);
    }

    @Test
    void testCreaCategorySenzaCampiObbligatori() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "descrizione": "",
                        "codice": "",
                        "budget": -1
                    }
                    """)
                .when()
                .post("/categories/")
                .then()
                .statusCode(400);
    }
}
