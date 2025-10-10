package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.ScaleChatMessageVisibility;
import br.hallel.relational.api.app.ministry.model.ScaleMessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.jcip.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "v_scale_chat_message_details")
@Getter @Setter
public class ScaleChatMessageResponseView {

    @Id
    private UUID id;

    private UUID eventScaleId;
    private UUID participantSenderId;

    // Mapeie os campos do usuário também
    private UUID userSenderId;

    private String content;

    @Enumerated(EnumType.STRING)
    private ScaleMessageType contentType;

    private OffsetDateTime sentAt;
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private ScaleChatMessageVisibility visibility;

    // IMPORTANTE: O status da view é mapeado como String
    private String aggregatedStatus;


}
