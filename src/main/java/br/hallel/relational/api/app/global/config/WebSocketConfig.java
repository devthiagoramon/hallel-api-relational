package br.hallel.relational.api.app.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/api");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] allowedOrigins = getAllowedOrigins();
        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        registry.addEndpoint("/ws-auth").setAllowedOrigins(allowedOrigins);
        registry.addEndpoint("/ws-payment").setAllowedOrigins(allowedOrigins);
        registry.addEndpoint("/ws-food-payments").setAllowedOrigins(allowedOrigins);

    }


    private String[] getAllowedOrigins() {
        List<String> origins = new ArrayList<>();

        // Origens fixas para desenvolvimento
        origins.add("http://localhost:5173");
        origins.add("http://localhost:3000");
        origins.add("https://comunidadecatolicahallel.com.br");

        // URL do ngrok (se disponível)
        String ngrokUrl = getNgrokUrl();
        if (ngrokUrl != null && !ngrokUrl.isEmpty()) {
            origins.add(ngrokUrl);
            System.out.println("🌐 WebSocket CORS: Ngrok URL detectada: " + ngrokUrl);
        } else {
            System.out.println("⚠️  WebSocket CORS: Ngrok não detectado, usando apenas localhost");
        }

        return origins.toArray(new String[0]);
    }


    private String getNgrokUrl() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(2000);
            factory.setReadTimeout(2000);
            restTemplate.setRequestFactory(factory);

            String response = restTemplate.getForObject("http://localhost:4040/api/tunnels", String.class);

            if (response != null) {
                // Parse simples para pegar a URL HTTPS
                String[] parts = response.split("\"public_url\":\"https://");
                if (parts.length > 1) {
                    String url = "https://" + parts[1].split("\"")[0];
                    return url;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  Ngrok não detectado na porta 4040");
        }
        return null;
    }
}
