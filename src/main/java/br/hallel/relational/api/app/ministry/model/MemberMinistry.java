package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "member_ministry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberMinistry {

    @EmbeddedId
    private MemberMinistryId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("ministryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;
}
