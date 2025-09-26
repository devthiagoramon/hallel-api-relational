package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventShortResponse {
    private UUID id;
    private String title;
    private Date date;
    private String fileImageUrl;
    private String banner;
    private boolean itsFree;
    private EventType type;
    private String local_event_name;
}

