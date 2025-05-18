package br.hallel.relational.api.app.ministry.model;

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

<<<<<<< HEAD
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("ministryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;


=======
>>>>>>> bfbe13ee13b1279d0c42d25833634fd487069bb7
}
