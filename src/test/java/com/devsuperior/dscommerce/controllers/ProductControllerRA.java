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

public class ProductControllerRA {

    private String clientUsername, clientPassword,adminUsername,adminPassword;
    private String clientToken,adminToken,invalidToken;
    private Long existingProductId,nonExistingProductId, dependentProductId;
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
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName(){
        postProductInstance.put("name","ab");
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
                .statusCode(422)
                .body("errors.message[0]",equalTo("Nome precisar ter de 3 a 80 caracteres"));
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription(){
        postProductInstance.put("description","ab");
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
                .statusCode(422)
                .body("errors.message[0]",equalTo("Descrição precisa ter no mínimo 10 caracteres"));
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceNegative(){
        postProductInstance.put("price",-1);
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
                .statusCode(422)
                .body("errors.message[0]",equalTo("O preço deve ser positivo"));
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero(){
        postProductInstance.put("price",0.0);
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
                .statusCode(422)
                .body("errors.message[0]",equalTo("O preço deve ser positivo"));
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndHasNotCategoryProduct(){
        postProductInstance.put("categories",null);
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
                .statusCode(422)
                .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"))
                .body("errors[0].fieldName", equalTo("categories"))
                .body("error", equalTo("Dados inválidos"))
                .body("path", equalTo("/products"));;
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndHasNotCategory(){
        postProductInstance.put("categories", null);
        JSONObject jsonObject = new JSONObject(postProductInstance);

        ValidatableResponse response = given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
        .then()
                .statusCode(422);

        System.out.println(response.extract().body().asString());
    }
    @Test
    public void insertShouldReturnUnprocessableEntityWhenBodyIsEmpty(){
        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body("{}")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
        .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Campo requerido"))
                .header("Content-Type", equalTo("application/json"))
                .header("Cache-Control", equalTo("no-cache, no-store, max-age=0, must-revalidate"));
    }
    @Test
    public void insertShouldReturnCreatedWhenValidProductIsProvided(){
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
                .body("id", notNullValue())
                .body("name", equalTo(postProductInstance.get("name")))
                .body("categories.size()", equalTo(2))
                .body("categories[0].id", equalTo(2))
                .body("categories[1].id", equalTo(3));
    }
    @Test
    public void insertShouldReturnForbiddenWhenClientLogged(){
        JSONObject jsonObject = new JSONObject(postProductInstance);

        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
        .then()
                .statusCode(403);
    }
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken(){
        JSONObject jsonObject = new JSONObject(postProductInstance);

        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
        .then()
                .statusCode(401);
    }
    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged(){
        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .delete("/products/{id}",existingProductId)
                .then()
                .statusCode(204);
    }
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged(){
       nonExistingProductId = 250000L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}",nonExistingProductId)
                .then()
                .statusCode(404);
    }
    @Test
    public void deleteShouldReturnBadRequestWhenIdDependentAndAdminLogged(){
        dependentProductId = 1L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}",dependentProductId)
                .then()
                .statusCode(400);
    }
    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged(){
        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .delete("/products/{id}",existingProductId)
                .then()
                .statusCode(403);
    }
    @Test
    public void deleteShouldReturnUnauthorizedWhenInvalidToken(){
        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .delete("/products/{id}",existingProductId)
                .then()
                .statusCode(401);
    }
}
