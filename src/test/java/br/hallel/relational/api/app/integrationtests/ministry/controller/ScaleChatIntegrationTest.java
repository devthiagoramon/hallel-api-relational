package br.hallel.relational.api.app.integrationtests.ministry.controller;


import br.hallel.relational.api.app.integrationtests.AbstractIntegrationTest;
import br.hallel.relational.api.app.integrationtests.auth.dto.LoginRequest;
import br.hallel.relational.api.app.integrationtests.auth.dto.TokenDTO;
import br.hallel.relational.api.app.integrationtests.auth.service.AuthServiceTest;
import br.hallel.relational.api.app.integrationtests.ministry.dto.TokenCoordinatorDTO;
import br.hallel.relational.api.app.integrationtests.ministry.service.AuthMinistryServiceTest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScaleChatIntegrationTest extends AbstractIntegrationTest implements WithAssertions {

    @LocalServerPort
    private int port;
    @Autowired
    private AuthServiceTest authService;
    @Autowired
    private AuthMinistryServiceTest authMinistryService;

    private static String userCoordinatorToken;
    private static String coordinatorToken;
    private static List<ScaleChatParticipantResponse> scaleChatParticipants = new ArrayList<>();


    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeAll
    public void setup() {
        TokenDTO coodinatorCredentials = authService.loginAndGetToken(port,
                new LoginRequest("barros@gmail.com", "barros123"));
        userCoordinatorToken = coodinatorCredentials.getAccessToken();
        TokenCoordinatorDTO tokenCoordinatorDTO = authMinistryService.getTokenCoordinator(port,
                "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190", "8675330f-9c78-4c6d-9230-b046b4097392");
        coordinatorToken = tokenCoordinatorDTO.getAccessToken();
    }

    private RequestSpecification getRequestSpecification(String url) {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBasePath(url)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .addHeader("Authorization", "Bearer " + userCoordinatorToken)
                .addHeader("coordenador-token", coordinatorToken)
                .build();
    }

    @Test
    @Order(1)
    public void createScaleChat() {

        String url = "/coordinator/event/scale/chat/808bb575-e8cb-4186-a91b-e13f992d9457";

        List<ScaleChatParticipantResponse> createdChatParticipants = RestAssured.given()
                .spec(getRequestSpecification(url))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().body().as(new TypeRef<List<ScaleChatParticipantResponse>>() {
                });

        assertThat(createdChatParticipants).isNotNull();
        assertThat(createdChatParticipants).isNotEmpty();
        assertThat(createdChatParticipants).hasSize(2);

        scaleChatParticipants = createdChatParticipants;
    }

    @Test
    @Order(2)
    public void removeParticipantOfScale() {

        String url = "/coordinator/event/scale/chat/remove/participant/" + scaleChatParticipants.getFirst().scaleParticipantId();

        RestAssured.given()
                .spec(getRequestSpecification(url))
                .when()
                .delete()
                .then()
                .statusCode(204).extract();


    }

    @Test
    @Order(3)
    public void addParticipantFromScaleChat() {
        String url = "/coordinator/event/scale/chat/add/participant";

        ScaleChatParticipant participantAdded = RestAssured.given()
                .spec(getRequestSpecification(url))
                .queryParam("user-id", "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190")
                .queryParam("scale-id", "808bb575-e8cb-4186-a91b-e13f992d9457")
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().body().as(ScaleChatParticipant.class);

        assertThat(participantAdded).isNotNull();
    }

    @Test
    @Order(4)
    public void addParticipantToScaleChatAgainError() {

        String url = "/coordinator/event/scale/chat/add/participant";

        RestAssured.given()
                .spec(getRequestSpecification(url))
                .queryParam("user-id", "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190")
                .queryParam("scale-id", "808bb575-e8cb-4186-a91b-e13f992d9457")
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract();

    }

    @Test
    @Order(5)
    public void deleteScaleChat(){
        String url = "/coordinator/event/scale/chat/808bb575-e8cb-4186-a91b-e13f992d9457";

       RestAssured.given()
                .spec(getRequestSpecification(url))
                .when()
                .delete()
                .then()
                .statusCode(204)
                .extract();
    }

    @Test
    @Order(6)
    public void addParticipantToScaleChatWhenChatNotCreated() {

        String url = "/coordinator/event/scale/chat/add/participant";

        RestAssured.given()
                .spec(getRequestSpecification(url))
                .queryParam("user-id", "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190")
                .queryParam("scale-id", "808bb575-e8cb-4186-a91b-e13f992d9457")
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract();

    }
}
