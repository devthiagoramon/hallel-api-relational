package br.hallel.relational.api.app.messaging.mobile.service;

import br.hallel.relational.api.app.messaging.mobile.config.FirebaseMessagingConfig;
import br.hallel.relational.api.app.messaging.mobile.exception.CredentialsFirebaseException;
import br.hallel.relational.api.app.messaging.mobile.exception.MessageFormatterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class FCMSenderService {

    private final FirebaseMessagingConfig config;

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = """
                    {
                      "message": {
                        "token": "%s",
                        "notification": {
                          "title": "%s",
                          "body": "%s"
                        },
                        "data": %s
                      }
                    }
                    """.formatted(fcmToken, title, body,
                    new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data));

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(config.getUrl(), request, String.class);
        } catch (JsonProcessingException e) {
            log.error("Error while sending notification", e);
            throw new MessageFormatterException("Can't send message for user, because has error in formatting");
        } catch (IOException e) {
            log.error("Error while sending notification", e);
            throw new CredentialsFirebaseException(
                    "Can't send message for user, because has error in reading credentials in API");
        }
    }

    private String getAccessToken() throws IOException {
        // 1. Carrega o arquivo de credenciais do diretório 'resources'.
        //    Certifique-se de que o nome do arquivo aqui é o mesmo que está em 'src/main/resources'.
        InputStream serviceAccountStream = getClass().getClassLoader()
                .getResourceAsStream("hallel-messaging-firebase.json");

        // 2. Garante que o arquivo foi encontrado, senão lança uma exceção clara.
        Objects.requireNonNull(serviceAccountStream,
                "Arquivo de credenciais 'crypto-avatar.json' não encontrado no classpath!");

        // 3. PONTO CRÍTICO: Cria as credenciais COM O ESCOPO CORRETO para o FCM.
        //    Diferente do Google Cloud Storage, para o FCM é obrigatório definir o escopo.
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/firebase.messaging"));

        // 4. Atualiza o token (a biblioteca gerencia se ele já expirou) e o retorna.
        credentials.refreshIfExpired();
        AccessToken accessToken = credentials.getAccessToken();

        return accessToken.getTokenValue();
    }
}