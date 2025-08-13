package br.hallel.relational.api.app.retreat;

import br.hallel.relational.api.app.event.model.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Retreat extends Event {
    @ElementCollection
    @CollectionTable(
            name = "retreat_schedule",
            joinColumns = @JoinColumn(name = "retreat_id")
    )
    @Column(name = "activity")
    private List<String> schedule;

    @OneToMany(mappedBy = "retreat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RetreatTransaction> transactions;
}
