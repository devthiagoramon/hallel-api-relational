package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ScaleEventWithEventInfoResponse {
    private UUID id;
    private EventShortResponse evento;
    private UUID ministerioId;
    private Date date;

}
