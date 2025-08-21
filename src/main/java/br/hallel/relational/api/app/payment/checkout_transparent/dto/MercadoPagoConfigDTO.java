package br.hallel.relational.api.app.payment.checkout_transparent.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter@Setter
@NoArgsConstructor@AllArgsConstructor

public class MercadoPagoConfigDTO {

    // A ID da notificação.
    private Long id;

    // O tipo da notificação, como "payment".
    private String type;

    // A ID do recurso, como a ID do pagamento.
    @JsonProperty("data")
    private DataDTO data;

    // O status da ação, como "payment.updated".
    private String action;

    // Outros campos úteis que podem ser enviados.
    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    private Long user_id;

    @Getter
    @Setter
    @ToString
    public static class DataDTO {

        private String id;
    }
}
