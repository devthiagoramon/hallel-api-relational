package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MinistryMemberRoleIds {
    @Column(name = "member_ministry_id")
    private UUID memberMinistryId;
    @Column(name = "role_ministry_id")
    private UUID roleMinistryId;
}
