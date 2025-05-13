package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor @AllArgsConstructor
public class NotConfirmedScaleDTO {
    private UUID idMemberMinistry;
    private String reason;
}
