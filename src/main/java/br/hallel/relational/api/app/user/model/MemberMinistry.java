package br.hallel.relational.api.app.user.model;

import br.hallel.relational.api.app.ministry.model.Ministry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "member_ministry")
@Entity
public class MemberMinistry {

    @EmbeddedId
    private MemberMinistryId id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;
}
