package br.hallel.relational.api.app.messaging.mobile.service;

import br.hallel.relational.api.app.messaging.mobile.config.FirebaseMessagingConfig;
import br.hallel.relational.api.app.messaging.mobile.exception.MessageFormatterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class FCMSenderService {

    private final FirebaseMessagingConfig config;

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(config.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String json = """
                    {
                      "to": "%s",
                      "notification": {
                        "title": "%s",
                        "body": "%s"
                      },
                      "data": %s
                    }
                    """.formatted(fcmToken, title, body,
                    new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data));

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(config.getUrl(), request, String.class);
        } catch (JsonProcessingException e) {
            log.error("Error while sending notification", e);
            throw new MessageFormatterException("Can't send message for user, because has error in formatting");
        }
    }
}