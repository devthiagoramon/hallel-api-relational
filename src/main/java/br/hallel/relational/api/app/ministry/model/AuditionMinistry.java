package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.EventScale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor @NoArgsConstructor

@Table(name = "audition_ministry")
@Entity
public class AuditionMinistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private Date date;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @OneToOne
    @JoinColumn(name = "event_scale_id")
    private EventScale eventScale;
}
