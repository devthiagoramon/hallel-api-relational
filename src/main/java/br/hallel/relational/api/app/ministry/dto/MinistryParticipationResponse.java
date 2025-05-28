package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.StatusParticipationMinistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinistryParticipationResponse {
    private UUID id;
    private String title;
    private String image;
    private StatusParticipationMinistry statusParticipation;
}
