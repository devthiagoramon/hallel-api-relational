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

}
