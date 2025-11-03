package br.hallel.relational.api.app.event.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "event_invite_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventInviteBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "max_number", nullable = false)
    private int maxNumber;

    @Column(name = "value_increase", nullable = false)
    private Double valueIncrease;
}
