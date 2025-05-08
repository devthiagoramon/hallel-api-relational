package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.MinistryScale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "event")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String local_event_name;

    @Column(nullable = false)
    private Double local_event_longitude;

    @Column(nullable = false)
    private Double local_event_latitude;

    @Column(nullable = false)
    private String image_url;

    @Column(nullable = false)
    private String banner_url;

    @Column(nullable = false)
    private Boolean isImportant;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String date_time;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "ministry_association",
//            joinColumns = @JoinColumn(name = "ministry_id"),
//            inverseJoinColumns = @JoinColumn(name = "ministry_id"))
//    private List<Ministry> ministriesAssociated;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "scale_ministries",
//            joinColumns = @JoinColumn(name = "ministry_scale_id"),
//            inverseJoinColumns = @JoinColumn(name = "ministry_scale_id")
//    )
//    private List<MinistryScale> ministriesScales;
}
