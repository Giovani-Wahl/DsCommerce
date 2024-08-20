package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtilRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {
    private String clientUsername, clientPassword,adminUsername,adminPassword,adminOnlyUsername, adminOnlyPassword;
    private String clientToken,adminToken,adminOnlyToken,invalidToken;
    private Long existingOrderId,nonExistingOrderId;

    private Map<String, List<Map<String, Object>>> postOrderInstance;

    @BeforeEach
    public void setUp(){
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        adminOnlyUsername = "ana@gmail.com";
        adminOnlyPassword = "123456";

        clientToken = TokenUtilRestAssured.obtainAccessToken(clientUsername,clientPassword);
        adminToken = TokenUtilRestAssured.obtainAccessToken(adminUsername,adminPassword);
        adminOnlyToken = TokenUtilRestAssured.obtainAccessToken(adminOnlyUsername, adminOnlyPassword);
        invalidToken = clientToken +"invalid";

        existingOrderId = 1L;
        nonExistingOrderId = 99999L;

        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1);
        item1.put("quantity", 2);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 5);
        item2.put("quantity", 1);

        List<Map<String,Object>> itemInstances = new ArrayList<>();
        itemInstances.add(item1);
        itemInstances.add(item2);

        postOrderInstance = new HashMap<>();
        postOrderInstance.put("items", itemInstances);
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged(){
        ValidatableResponse response = given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}",existingOrderId)
        .then()
                .statusCode(200)
                .body("id",is(1))
                .body("status",equalTo("PAID"))
                .body("client.name",equalTo("Maria Brown"))
                .body("items.name",hasItems("The Lord of the Rings","Macbook Pro"))
                .body("total",is(1431.0F));
        System.out.println(response.extract().body().asString());
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientOwnerLogged(){
        ValidatableResponse response = given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}",existingOrderId)
        .then()
                .statusCode(200)
                .body("id",is(1))
                .body("status",equalTo("PAID"))
                .body("client.name",equalTo("Maria Brown"))
                .body("items.name",hasItems("The Lord of the Rings","Macbook Pro"))
                .body("total",is(1431.0F));
        System.out.println(response.extract().body().asString());
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndOtherClientLogged(){
        existingOrderId = 2L;
        ValidatableResponse response = given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}",existingOrderId)
        .then()
                .statusCode(403);
        System.out.println(response.extract().body().asString());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged(){
        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}",nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged(){
        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}",nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken(){
        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}",existingOrderId)
                .then()
                .statusCode(401);
    }

    @Test
    public void insertShouldReturnOrderCreatedWhenClientLogged() throws JSONException {
        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
        .when()
                .post("/orders")
        .then()
                .statusCode(201)
                .body("status", equalTo("WAITING_PAYMENT"))
                .body("client.name", equalTo("Maria Brown"))
                .body("items.name", hasItems("The Lord of the Rings", "Rails for Dummies"))
                .body("total", is(281.99F));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenClientLoggedAndOrderHasNoItem() throws Exception {
        postOrderInstance.put("items", null);
        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
        .when()
                .post("/orders")
        .then()
                .statusCode(422);
    }

    @Test
    public void insertShouldReturnForbiddenWhenAdminLogged() throws JSONException {
        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminOnlyToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
        .when()
                .post("/orders")
        .then()
                .statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws JSONException {
        JSONObject newOrder = new JSONObject(postOrderInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newOrder)
        .when()
                .post("/orders")
        .then()
                .statusCode(401);
    }
}
