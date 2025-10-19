package br.hallel.relational.api.app.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Define o tempo máximo para estabelecer a conexão com o servidor (ex: 5 segundos)
                .requestFactorySettings(clientHttpRequestFactorySettings ->
                        clientHttpRequestFactorySettings.withConnectTimeout(Duration.ofSeconds(5))
                                .withReadTimeout(Duration.ofSeconds(10))).build();
    }
}
