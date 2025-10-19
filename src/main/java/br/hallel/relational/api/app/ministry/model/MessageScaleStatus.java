package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "message_scale_status")
@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageScaleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id")
    private ScaleChatMessage message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipíent_chat_id")
    private ScaleChatParticipant chatParticipant;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private MessageScaleDeliveryStatus status;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public MessageScaleStatus(ScaleChatMessage message, ScaleChatParticipant chatParticipant) {
        this.message = message;
        this.chatParticipant = chatParticipant;
    }
}
