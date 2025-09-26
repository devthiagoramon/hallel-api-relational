package br.hallel.relational.api.app.integrationtests.ministry.controller;

import br.hallel.relational.api.app.event.exception.EventParticipationException;
import br.hallel.relational.api.app.integrationtests.AbstractIntegrationTest;
import br.hallel.relational.api.app.integrationtests.auth.dto.LoginRequest;
import br.hallel.relational.api.app.integrationtests.auth.dto.TokenDTO;
import br.hallel.relational.api.app.integrationtests.auth.service.AuthServiceTest;
import br.hallel.relational.api.app.integrationtests.ministry.dto.PaginationTestResponse;
import br.hallel.relational.api.app.integrationtests.ministry.dto.TokenCoordinatorDTO;
import br.hallel.relational.api.app.integrationtests.ministry.service.AuthMinistryServiceTest;
import br.hallel.relational.api.app.integrationtests.ministry.service.ScaleChatServiceTest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse;
import br.hallel.relational.api.app.ministry.model.MessageScaleDeliveryStatus;
import br.hallel.relational.api.app.ministry.model.MessageScaleStatus;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.model.ScaleMessageType;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScaleMessageChatIntegrationTest extends AbstractIntegrationTest implements WithAssertions {

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
    private static List<ScaleChatParticipantResponse> scaleChatParticipants = new ArrayList<>();
    private static ScaleChatMessageResponse scaleChatMessageResponse;

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeAll
    public void setup() {
        TokenDTO coodinatorCredentials = authService.loginAndGetToken(port,
                new LoginRequest("barros@gmail.com", "barros123"));
        userCoordinatorToken = coodinatorCredentials.getAccessToken();
        TokenDTO userCredentials = authService.loginAndGetToken(port,
                new LoginRequest("devmiguelarcanjo@gmail.com", "miguel123"));
        userToken = userCredentials.getAccessToken();
        TokenCoordinatorDTO tokenCoordinatorDTO = authMinistryService.getTokenCoordinator(port,
                "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190", "8675330f-9c78-4c6d-9230-b046b4097392");
        coordinatorToken = tokenCoordinatorDTO.getAccessToken();
        List<ScaleChatParticipantResponse> participantsResponse = scaleChatService.createScaleTest(
                UUID.fromString("808bb575-e8cb-4186-a91b-e13f992d9457"));
        scaleChatParticipants = participantsResponse.stream().toList();
    }


    private RequestSpecification getRequestSpecification(String url) {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBasePath(url)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .addHeader("Authorization", "Bearer " + userCoordinatorToken)
                .build();
    }

    private RequestSpecification getRequestSpecificationAsMember(String url) {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBasePath(url)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .addHeader("Authorization", "Bearer " + userToken)
                .build();
    }

    @Test
    @Order(1)
    public void sendMessageForEveryoneAndVerifyIfReceive() {
        String url = "/user/event/scale/chat/message/text";


        UUID memberChatSenderId = scaleChatParticipants
                .getFirst()
                .userParticipant()
                .getId()
                .toString()
                .equals("fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190") ?
                scaleChatParticipants
                        .getFirst()
                        .scaleParticipantId()
                : null;
        if (memberChatSenderId == null) {
            throw new EventParticipationException("Not found user");
        }
        ScaleChatMessageResponse message = RestAssured.given()
                .spec(getRequestSpecification(url))
                .body(new ScaleChatMessageRequest(UUID.fromString("808bb575-e8cb-4186-a91b-e13f992d9457"),
                        memberChatSenderId, "Mensagem teste", ScaleMessageType.TEXT))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().body().as(ScaleChatMessageResponse.class);


        assertThat(message).isNotNull();
        assertThat(message.content()).isEqualTo("Mensagem teste");
        assertThat(message.contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(message.participantSenderId()).isEqualTo(memberChatSenderId);
        assertThat(message.sentAt()).isNotNull();
        assertThat(message.updatedAt()).isNull();

        scaleChatMessageResponse = message;
    }

    @Test
    @Order(2)
    public void listMessagesAsSender() {
        String url = "/user/event/scale/chat/message/808bb575-e8cb-4186-a91b-e13f992d9457";

        PaginationTestResponse<ScaleChatMessageResponse> paginationTestResponse = RestAssured
                .given()
                .spec(getRequestSpecification(url))
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<PaginationTestResponse<ScaleChatMessageResponse>>() {
                });

        assertThat(paginationTestResponse.getPage().getSize()).isEqualTo(20);
        assertThat(paginationTestResponse.getPage().getNumber()).isEqualTo(0);
        assertThat(paginationTestResponse.getPage().getTotalElements()).isEqualTo(1);
        assertThat(paginationTestResponse.getContent().size()).isEqualTo(1);
        assertThat(paginationTestResponse.getContent().getFirst().contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(paginationTestResponse.getContent().getFirst().content()).isEqualTo("Mensagem teste");
        assertThat(paginationTestResponse.getContent().getFirst().statusMessage()).isEqualTo(
                MessageScaleDeliveryStatus.READ);
    }

    @Test
    @Order(3)
    public void listMessagesAsParticipant() {
        String url = "/user/event/scale/chat/message/808bb575-e8cb-4186-a91b-e13f992d9457";

        PaginationTestResponse<ScaleChatMessageResponse> paginationTestResponse = RestAssured
                .given()
                .spec(getRequestSpecificationAsMember(url))
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<PaginationTestResponse<ScaleChatMessageResponse>>() {
                });

        assertThat(paginationTestResponse.getPage().getSize()).isEqualTo(20);
        assertThat(paginationTestResponse.getPage().getNumber()).isEqualTo(0);
        assertThat(paginationTestResponse.getPage().getTotalElements()).isEqualTo(1);
        assertThat(paginationTestResponse.getContent().size()).isEqualTo(1);
        assertThat(paginationTestResponse.getContent().getFirst().contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(paginationTestResponse.getContent().getFirst().content()).isEqualTo("Mensagem teste");
        assertThat(paginationTestResponse.getContent().getFirst().statusMessage()).isEqualTo(
                MessageScaleDeliveryStatus.SENT);

    }

    @Test
    @Order(4)
    public void readMessagesAsParticipant() {
        String url = "/user/event/scale/chat/message/read-message/" + scaleChatMessageResponse.id();

        MessageScaleDeliveryStatus responseStatus = RestAssured
                .given()
                .spec(getRequestSpecificationAsMember(url))
                .when()
                .patch()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<MessageScaleDeliveryStatus>() {
                });

        assertThat(responseStatus).isNotNull();
        assertThat(responseStatus).isEqualTo(MessageScaleDeliveryStatus.READ);

    }

    @Test
    @Order(5)
    public void listMessagesAsParticipantWhenReadMessage() {
        String url = "/user/event/scale/chat/message/808bb575-e8cb-4186-a91b-e13f992d9457";

        PaginationTestResponse<ScaleChatMessageResponse> paginationTestResponse = RestAssured
                .given()
                .spec(getRequestSpecificationAsMember(url))
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<PaginationTestResponse<ScaleChatMessageResponse>>() {
                });

        assertThat(paginationTestResponse.getPage().getSize()).isEqualTo(20);
        assertThat(paginationTestResponse.getPage().getNumber()).isEqualTo(0);
        assertThat(paginationTestResponse.getPage().getTotalElements()).isEqualTo(1);
        assertThat(paginationTestResponse.getContent().size()).isEqualTo(1);
        assertThat(paginationTestResponse.getContent().getFirst().contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(paginationTestResponse.getContent().getFirst().content()).isEqualTo("Mensagem teste");
        assertThat(paginationTestResponse.getContent().getFirst().statusMessage()).isEqualTo(
                MessageScaleDeliveryStatus.READ);

    }

    @Test
    @Order(6)
    public void sendAnotherMessageAsMember() {
        String url = "/user/event/scale/chat/message/text";


        UUID memberChatSenderId = scaleChatParticipants
                .get(1)
                .userParticipant()
                .getId()
                .toString()
                .equals("a78319c9-abd5-48d0-988a-60f421e9dd98") ?
                scaleChatParticipants
                        .get(1)
                        .scaleParticipantId()
                : null;
        if (memberChatSenderId == null) {
            throw new EventParticipationException("Not found user");
        }
        ScaleChatMessageResponse message = RestAssured.given()
                .spec(getRequestSpecificationAsMember(url))
                .body(new ScaleChatMessageRequest(UUID.fromString("808bb575-e8cb-4186-a91b-e13f992d9457"),
                        memberChatSenderId, "Mensagem teste miguel", ScaleMessageType.TEXT))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().body().as(ScaleChatMessageResponse.class);


        assertThat(message).isNotNull();
        assertThat(message.content()).isEqualTo("Mensagem teste miguel");
        assertThat(message.contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(message.participantSenderId()).isEqualTo(memberChatSenderId);
        assertThat(message.sentAt()).isNotNull();
        assertThat(message.updatedAt()).isNull();

        scaleChatMessageResponse = message;
    }

    @Test
    @Order(7)
    public void receiveMessagesAsCoordinator() {
        String url = "/user/event/scale/chat/message/receive-message/" + scaleChatMessageResponse.id();

        MessageScaleDeliveryStatus responseStatus = RestAssured
                .given()
                .spec(getRequestSpecification(url))
                .when()
                .patch()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<MessageScaleDeliveryStatus>() {
                });

        assertThat(responseStatus).isNotNull();
        assertThat(responseStatus).isEqualTo(MessageScaleDeliveryStatus.RECEIVED);

    }

    @Test
    @Order(8)
    public void listMessagesAsCoordinatorWhenReceiveMessage() {
        String url = "/user/event/scale/chat/message/808bb575-e8cb-4186-a91b-e13f992d9457";

        PaginationTestResponse<ScaleChatMessageResponse> paginationTestResponse = RestAssured
                .given()
                .spec(getRequestSpecification(url))
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<PaginationTestResponse<ScaleChatMessageResponse>>() {
                });

        assertThat(paginationTestResponse.getPage().getSize()).isEqualTo(20);
        assertThat(paginationTestResponse.getPage().getNumber()).isEqualTo(0);
        assertThat(paginationTestResponse.getPage().getTotalElements()).isEqualTo(2);
        assertThat(paginationTestResponse.getContent().size()).isEqualTo(2);
        assertThat(paginationTestResponse.getContent().getFirst().contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(paginationTestResponse.getContent().getFirst().content()).isEqualTo("Mensagem teste miguel");
        assertThat(paginationTestResponse.getContent().getFirst().statusMessage()).isEqualTo(
                MessageScaleDeliveryStatus.RECEIVED);
    }

    @Test
    @Order(9)
    public void sendFiftyMessagesToChat() {

        String url = "/user/event/scale/chat/message/text";


        UUID memberChatSenderId = scaleChatParticipants
                .get(1)
                .userParticipant()
                .getId()
                .toString()
                .equals("a78319c9-abd5-48d0-988a-60f421e9dd98") ?
                scaleChatParticipants
                        .get(1)
                        .scaleParticipantId()
                : null;
        if (memberChatSenderId == null) {
            throw new EventParticipationException("Not found user");
        }
        for (int i = 0; i < 50; i++) {

            RestAssured.given()
                    .spec(getRequestSpecificationAsMember(url))
                    .body(new ScaleChatMessageRequest(UUID.fromString("808bb575-e8cb-4186-a91b-e13f992d9457"),
                            memberChatSenderId, "Mensagem teste mockada " + (i+1), ScaleMessageType.TEXT))
                    .when()
                    .post()
                    .then()
                    .statusCode(201)
                    .extract().body().as(ScaleChatMessageResponse.class);
        }
    }

    @Test
    @Order(10)
    public void listMessagesAsCoordinatorWhenSendedFiftyMessages() {
        String url = "/user/event/scale/chat/message/808bb575-e8cb-4186-a91b-e13f992d9457";

        PaginationTestResponse<ScaleChatMessageResponse> paginationTestResponse = RestAssured
                .given()
                .spec(getRequestSpecification(url))
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(new TypeRef<PaginationTestResponse<ScaleChatMessageResponse>>() {
                });

        assertThat(paginationTestResponse.getPage().getSize()).isEqualTo(20);
        assertThat(paginationTestResponse.getPage().getNumber()).isEqualTo(0);
        assertThat(paginationTestResponse.getPage().getTotalElements()).isEqualTo(52);
        assertThat(paginationTestResponse.getContent().size()).isEqualTo(20);
        assertThat(paginationTestResponse.getContent().getFirst().contentType()).isEqualTo(ScaleMessageType.TEXT);
        assertThat(paginationTestResponse.getContent().getFirst().content()).isEqualTo("Mensagem teste mockada 50");
        assertThat(paginationTestResponse.getContent().getFirst().statusMessage()).isEqualTo(
                MessageScaleDeliveryStatus.SENT);
    }

}
