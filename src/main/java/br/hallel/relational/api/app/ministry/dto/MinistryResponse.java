package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MinistryType;
import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinistryResponse {
    private UUID id;
    private String title;
    private String description;
    private String image;
    private Boolean hasRepertoire;
    private MinistryType ministryType;
    private MemberMinistry coordinator;
    private MemberMinistry viceCoordinator;
}
