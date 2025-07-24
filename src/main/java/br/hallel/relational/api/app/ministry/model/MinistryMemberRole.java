package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "ministry_member_role")
@Entity
public class MinistryMemberRole {

    @EmbeddedId
    private MinistryMemberRoleIds ministryMemberRoleIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_ministry_id", insertable = false, updatable = false)
    private MemberMinistry memberMinistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_ministry_id", insertable = false, updatable = false)
    private RoleMinistry roleMinistry;

    public MinistryMemberRole(MinistryMemberRoleIds ministryMemberRoleIds) {
        this.ministryMemberRoleIds = ministryMemberRoleIds;
    }
}
