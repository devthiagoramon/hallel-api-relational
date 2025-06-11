package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "guest_invited_event_scale")
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class GuestInvitedEventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, name = "name")
    private String name;
    @Column(nullable = false, name = "email")
    private String email;
    @Column(nullable = false, name = "phone")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventScale eventScale;
    @ManyToOne(fetch = FetchType.LAZY)
    private InviteEventScale inviteEventScale;
}
