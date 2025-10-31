package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.model.enum_type.EventStatus;
import br.hallel.relational.api.app.event.model.enum_type.EventType;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
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
    private List<EventSchedule> schedules;
    private List<EventInvite> eventInvites;
    private List<MinistrySimpleResponse> ministriesAssocied;
    private EventType eventType;
    private EventStatus eventStatus;

    public static EventResponse eventToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getBanner_url(),
                event.getImage_url(),
                event.getIsImportant(),
                event.getLocal_event_name(),
                event.getLocal_event_longitude(),
                event.getLocal_event_latitude(),
                event.getEventSchedules(),
                event.getEventInvites(),
                null,
                event.getEventType(),
                event.getEventStatus()
        );
    }
}
