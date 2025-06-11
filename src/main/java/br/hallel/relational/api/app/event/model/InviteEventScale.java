package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Table(name = "invite_event_scale")
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class InviteEventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "is_sent")
    private boolean isSent;
    @Column(name = "message")
    private String message;
    @Column(name = "date_send")
    private Date dateSend;
    @Column(name = "date_edit")
    private Date dateEdit;

}
