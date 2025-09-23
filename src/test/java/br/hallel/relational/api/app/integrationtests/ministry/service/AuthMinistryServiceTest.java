package br.hallel.relational.api.app.integrationtests.ministry.service;

import br.hallel.relational.api.app.integrationtests.ministry.dto.TokenCoordinatorDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthMinistryServiceTest {

    private static final String AUTH_COORDINATOR_URL = "/auth/ministry/generate-token";


    public TokenCoordinatorDTO getTokenCoordinator(int port, String userId, String ministryId) {
        return RestAssured.given()
                .basePath(AUTH_COORDINATOR_URL)
                .port(port)
                .contentType(ContentType.JSON)
                .param("userId", userId)
                .param("ministryId", ministryId)
                .when()
                .get()
                .then()
                .statusCode(201)
                .extract().body().as(TokenCoordinatorDTO.class);

    }
}
