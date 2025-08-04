package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ScaleChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipíent_chat_id")
    private ScaleChatParticipant chatParticipant;



}
