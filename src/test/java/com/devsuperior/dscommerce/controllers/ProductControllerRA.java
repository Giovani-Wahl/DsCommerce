package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtilRestAssured;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private String clientUsername, clientPassword,adminUsername,adminPassword;
    private String clientToken,adminToken,invalidToken;
    private Long existingProductId,nonExistingProductId;
    private String productName;

    private Map<String,Object> postProductInstance;

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

        List<Map<String,Object>> categories = new ArrayList<>();

        Map<String,Object> category2 = new HashMap<>();
        category2.put("id",2);
        Map<String,Object> category3 = new HashMap<>();
        category3.put("id",3);

        categories.add(category2);
        categories.add(category3);

        postProductInstance = new HashMap<>();
        postProductInstance.put("name","meu novo produto");
        postProductInstance.put("description","a descrição do produto a ser inserido cadastrado");
        postProductInstance.put("imgUrl","link da imagem do pŕoduto");
        postProductInstance.put("price",50.0);
        postProductInstance.put("categories",categories);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists(){
        existingProductId = 2L;

        given()
                .get("/products/{id}",existingProductId)
        .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name",equalTo("Smart TV"))
                .body("price",is(2190.0F))
                .body("categories.id",hasItems(2,3))
                .body("categories.name",hasItems("Eletrônicos","Computadores"));
    }
    @Test
    public void findAllShouldReturnPageWhenNameParamIsEmpty(){
        given()
                .get("/products")
        .then()
                .statusCode(200)
                .body("content.name",hasItems("Macbook Pro","PC Gamer Tera"));
    }
    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty(){
        productName = "Macbook";

        given()
                .get("/products?name={productName}",productName)
        .then()
                .statusCode(200)
                .body("content[0].id",is(3))
                .body("content[0].name",equalTo("Macbook Pro"))
                .body("content[0].price",is(1250.0F));
    }
    @Test
    public void findAllShouldReturnPagedProductsWithPriceGreatThan2000(){
        given().
                get("/products?size=25").
        then().
                statusCode(200).
                body("content.findAll{it.price>2000}.name",hasItems("Smart TV","PC Gamer Weed"));
    }
    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged(){
        JSONObject jsonObject = new JSONObject(postProductInstance);
        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
        .then()
                .statusCode(201)
                .body("name",equalTo("meu novo produto"))
                .body("price",is(50.0F))
                .body("categories.id",hasItems(2,3));
    }
}
