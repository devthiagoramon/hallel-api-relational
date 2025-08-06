package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.EventScale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "scale_chat_message")
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScaleChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_scale_id")
    private EventScale scale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_chat_sender_id")
    private ScaleChatParticipant memberChatSender;

    @Column(nullable = false, name = "content")
    private String content;

    @Column(name = "content_type", nullable = false)
    private ScaleMessageType contentType;

    @Column(nullable = false, name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "visibility")
    private ScaleChatMessageVisibility visibility;

    public ScaleChatMessage(EventScale scale, ScaleChatParticipant memberChatSender) {
        this.scale = scale;
        this.memberChatSender = memberChatSender;
    }

    public ScaleChatMessage(EventScale scale, ScaleChatParticipant memberChatSender, String content,
                            ScaleMessageType contentType) {
        this.scale = scale;
        this.memberChatSender = memberChatSender;
        this.content = content;
        this.contentType = contentType;
    }

    public ScaleChatMessage(EventScale scale, ScaleChatParticipant memberChatSender, ScaleMessageType contentType) {
        this.scale = scale;
        this.memberChatSender = memberChatSender;
        this.contentType = contentType;
    }
}
