package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "member_ministry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberMinistry {

    @EmbeddedId
    private MemberMinistryId id;


}
