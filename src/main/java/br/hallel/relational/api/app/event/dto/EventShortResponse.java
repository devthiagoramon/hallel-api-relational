package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.enum_type.EventStatus;
import br.hallel.relational.api.app.event.model.enum_type.EventType;
import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventShortResponse {
    private UUID id;
    private String title;
    private Date date;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String fileImageUrl;
    private String banner;
    private boolean itsFree;
    private EventType type;
    private String local_event_name;
    private EventStatus status;
}

