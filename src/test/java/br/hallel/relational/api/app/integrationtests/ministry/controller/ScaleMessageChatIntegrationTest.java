package br.hallel.relational.api.app.integrationtests.ministry.controller;

import br.hallel.relational.api.app.integrationtests.AbstractIntegrationTest;
import br.hallel.relational.api.app.integrationtests.auth.dto.LoginRequest;
import br.hallel.relational.api.app.integrationtests.auth.dto.TokenDTO;
import br.hallel.relational.api.app.integrationtests.auth.service.AuthServiceTest;
import br.hallel.relational.api.app.integrationtests.ministry.dto.TokenCoordinatorDTO;
import br.hallel.relational.api.app.integrationtests.ministry.service.AuthMinistryServiceTest;
import br.hallel.relational.api.app.integrationtests.ministry.service.ScaleChatServiceTest;
import br.hallel.relational.api.app.integrationtests.ministry.service.ScaleMessageChatServiceTest;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScaleMessageChatIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private AuthServiceTest authService;
    @Autowired
    private AuthMinistryServiceTest authMinistryService;
    @Autowired
    private ScaleChatServiceTest scaleChatService;


    private static String userCoordinatorToken;
    private static String coordinatorToken;
    private static String userToken;
    private static List<ScaleChatParticipant> scaleChatParticipants = new ArrayList<>();


    @BeforeEach
    void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeAll
    public void setup(){
        TokenDTO coodinatorCredentials = authService.loginAndGetToken(port, new LoginRequest("barros@gmail.com", "barros123"));
        userCoordinatorToken = coodinatorCredentials.getAccessToken();
        TokenDTO userCredentials = authService.loginAndGetToken(port, new LoginRequest("miguel@gmail.com", "miguel123"));
        userToken = userCredentials.getAccessToken();
        TokenCoordinatorDTO tokenCoordinatorDTO = authMinistryService.getTokenCoordinator(port, "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190", "8675330f-9c78-4c6d-9230-b046b4097392");
        coordinatorToken = tokenCoordinatorDTO.getAccessToken();
        List<ScaleChatParticipant> participantsResponse = scaleChatService.createScaleTest(
                UUID.fromString("808bb575-e8cb-4186-a91b-e13f992d9457"));
        scaleChatParticipants = participantsResponse.stream().toList();
    }





}
