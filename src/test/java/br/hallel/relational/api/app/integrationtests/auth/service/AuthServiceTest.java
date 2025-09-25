package br.hallel.relational.api.app.integrationtests.auth.service;

import br.hallel.relational.api.app.integrationtests.auth.dto.LoginRequest;
import br.hallel.relational.api.app.integrationtests.auth.dto.TokenDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceTest {

    private static final String AUTH_ENDPOINT = "/auth/login";

    public TokenDTO loginAndGetToken(int port, LoginRequest loginRequest) {
        return RestAssured.given()
                .basePath(AUTH_ENDPOINT)
                .port(port)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(TokenDTO.class);
    }
}