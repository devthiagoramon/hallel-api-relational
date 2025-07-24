package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity(name = "MemberMinistry")
@Table(name = "member_ministry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberMinistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ministry_member_role",
            joinColumns = @JoinColumn(name = "member_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "role_ministry_id"))
    private List<RoleMinistry> ministryRoles;

    public MemberMinistry(User user, Ministry ministry) {
        this.user = user;
        this.ministry = ministry;
    }
}
