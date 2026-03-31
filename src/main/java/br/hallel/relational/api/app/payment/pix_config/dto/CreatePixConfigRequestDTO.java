package br.hallel.relational.api.app.payment.pix_config.dto;

import br.hallel.relational.api.app.payment.pix_config.model.TipoChavePix;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePixConfigRequestDTO(

        @NotBlank(message = "A chave PIX é obrigatória")
        @Size(max = 255, message = "A chave PIX deve ter no máximo 255 caracteres")
        String chavePix,

        @NotNull(message = "O tipo de chave é obrigatório")
        TipoChavePix tipoChave,

        @NotBlank(message = "O nome do banco é obrigatório")
        @Size(max = 100, message = "O nome do banco deve ter no máximo 100 caracteres")
        String nomeBanco,

        @NotBlank(message = "O nome do recebedor é obrigatório")
        @Size(max = 150, message = "O nome do recebedor deve ter no máximo 150 caracteres")
        String nomeRecebedor,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,

        @Size(max = 500, message = "O token deve ter no máximo 500 caracteres")
        String mercadoPagoAccessToken
) {}
