package br.hallel.relational.api.app.ministry.model;

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
    @JoinColumn(name = "member_ministry_id", updatable = false, insertable = false)
    private MemberMinistry memberMinistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("functionMinistryId")
    @JoinColumn(name = "function_ministry_id", updatable = false, insertable = false)
    private FunctionMinistry functionMinistry;


}
