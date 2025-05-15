package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "function_ministry_member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FunctionMinistryMember {

    @EmbeddedId
    private FunctionMinistryMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("functionMinistryId")
    @JoinColumn(name = "function_ministry_id")
    private FunctionMinistry functionMinistry;
}
