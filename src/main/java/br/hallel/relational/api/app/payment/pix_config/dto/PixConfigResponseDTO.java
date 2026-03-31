package br.hallel.relational.api.app.payment.pix_config.dto;

import br.hallel.relational.api.app.payment.pix_config.model.PixConfig;
import br.hallel.relational.api.app.payment.pix_config.model.TipoChavePix;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PixConfigResponseDTO {

    private UUID id;
    private String chavePix;
    private TipoChavePix tipoChave;
    private String nomeBanco;
    private String nomeRecebedor;
    private String descricao;
    private boolean hasToken;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static PixConfigResponseDTO toResponse(PixConfig pixConfig) {
        return PixConfigResponseDTO.builder()
                .id(pixConfig.getId())
                .chavePix(pixConfig.getChavePix())
                .tipoChave(pixConfig.getTipoChave())
                .nomeBanco(pixConfig.getNomeBanco())
                .nomeRecebedor(pixConfig.getNomeRecebedor())
                .descricao(pixConfig.getDescricao())
                .hasToken(pixConfig.getMercadoPagoAccessToken() != null
                        && !pixConfig.getMercadoPagoAccessToken().isBlank())
                .ativo(pixConfig.isAtivo())
                .criadoEm(pixConfig.getCriadoEm())
                .atualizadoEm(pixConfig.getAtualizadoEm())
                .build();
    }
}
