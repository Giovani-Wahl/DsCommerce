package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtilRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {
    private String clientUsername, clientPassword,adminUsername,adminPassword;
    private String clientToken,adminToken,invalidToken;
    private Long existingOrderId,nonExistingOrderId;

    @BeforeEach
    public void setUp(){
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtilRestAssured.obtainAccessToken(clientUsername,clientPassword);
        adminToken = TokenUtilRestAssured.obtainAccessToken(adminUsername,adminPassword);
        invalidToken = clientToken +"invalid";

        existingOrderId = 1L;
        nonExistingOrderId = 99999L;
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
}
