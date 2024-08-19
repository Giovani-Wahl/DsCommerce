package com.devsuperior.dscommerce.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class TokenUtilRestAssured {

    private static Response authRequest(String username,String password){
        return given()
                .auth()
                .preemptive()
           .basic("myclientid","myclientsecret")
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", username)
                .formParam("password", password)
                .formParam("grant_type", "password")
           .when()
                .post("/oauth2/token");
    }

    public static String obtainAccessToken(String username,String password){
        Response response = authRequest(username, password);
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to obtain access token: " + response.getStatusLine());
        }
        JsonPath jsonBody = response.jsonPath();
        return jsonBody.getString("access_token");
    }
}
