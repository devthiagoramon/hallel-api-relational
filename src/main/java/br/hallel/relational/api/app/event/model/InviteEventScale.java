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

    public InviteEventScale(boolean isSent, String message, Date dateSend, Date dateEdit) {
        this.isSent = isSent;
        this.message = message;
        this.dateSend = dateSend;
        this.dateEdit = dateEdit;
    }
}
