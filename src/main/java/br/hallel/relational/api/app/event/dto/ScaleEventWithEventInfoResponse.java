package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ScaleEventWithEventInfoResponse {
    private String id;
    private EventShortResponse evento;
    private String ministerioId;
    private Date date;

}
