package br.hallel.relational.api.app.payment.checkout_transparent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MercadoPagoConfigDTO {

    private Long id;
    private String action;
    @JsonProperty("api_version")
    private String apiVersion;
    @JsonProperty("date_created")
    private String dateCreated;
    @JsonProperty("live_mode")
    private Boolean liveMode;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("data")
    private DataDTO data;

    private String resource;
    private String topic;
    private String type;


    @Data
    @NoArgsConstructor
    public static class DataDTO {
        private String id;
    }
}