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
class LayoutApiTest {

    private static String createdId;

    @Test
    @Order(1)
    void testCreateLayout() {
        createdId = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "layoutName": "Test Layout",
                        "isDefault": false,
                        "layoutItems": [
                            {
                                "i": "widget-1",
                                "x": 0,
                                "y": 0,
                                "w": 4,
                                "h": 2
                            },
                            {
                                "i": "widget-2",
                                "x": 4,
                                "y": 0,
                                "w": 4,
                                "h": 2
                            }
                        ]
                    }
                    """)
                .when()
                .post("/layouts/")
                .then()
                .statusCode(201)
                .header("Location", containsString("/layouts/"))
                .extract()
                .header("Location")
                .replaceAll(".*/layouts/", "");
    }

    @Test
    @Order(2)
    void testGetAllLayouts() {
        given()
                .when()
                .get("/layouts/all")
                .then()
                .statusCode(200)
                .body("$", is(not(empty())));
    }

    @Test
    @Order(3)
    void testGetLayoutById() {
        given()
                .when()
                .get("/layouts/" + createdId)
                .then()
                .statusCode(200)
                .body("layoutName", is("Test Layout"))
                .body("layoutItems", hasSize(2));
    }

    @Test
    @Order(4)
    void testUpdateLayout() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "layoutName": "Test Layout Updated",
                        "isDefault": true,
                        "layoutItems": [
                            {
                                "i": "widget-1",
                                "x": 0,
                                "y": 0,
                                "w": 6,
                                "h": 3
                            }
                        ]
                    }
                    """)
                .when()
                .put("/layouts/" + createdId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void testDeleteLayout() {
        given()
                .when()
                .delete("/layouts/" + createdId)
                .then()
                .statusCode(204);
    }

    @Test
    void testCreateLayoutSenzaNome() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "layoutName": "",
                        "layoutItems": []
                    }
                    """)
                .when()
                .post("/layouts/")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetLayoutByIdInesistente() {
        given()
                .when()
                .get("/layouts/000000000000000000000000")
                .then()
                .statusCode(anyOf(is(404), is(500)));
    }
}
