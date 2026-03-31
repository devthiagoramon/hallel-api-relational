package br.hallel.relational.api.app.payment.pix_config.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "pix_config")
public class PixConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "chave_pix", nullable = false)
    private String chavePix;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_chave", nullable = false)
    private TipoChavePix tipoChave;

    @Column(name = "nome_banco", nullable = false)
    private String nomeBanco;

    @Column(name = "nome_recebedor", nullable = false)
    private String nomeRecebedor;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "mercado_pago_access_token")
    private String mercadoPagoAccessToken;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = false;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
