package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private Date date;
    private String banner_url;
    private String image_url;
    private Boolean isImportant;
    private String local_event_name;
    private Double local_event_longitude;
    private Double local_event_latitude;
    private Double value;
}
