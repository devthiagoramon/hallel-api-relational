package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter
public class EventResponseWithMinistryAssociated {
    private UUID id;
    private String title;
    private String description;
    private Date date;
    private Duration duration;
    private String banner_url;
    private String image_url;
    private Boolean isImportant;
    private String local_event_name;
    private Double local_event_longitude;
    private Double local_event_latitude;
    private List<EventInvite> eventInvites;
    private List<EventSchedule> eventSchedules;
    private EventType eventType;
    private boolean itsFree;
    private EventStatus eventStatus;
    private List<MinistryResponse> ministries;
    private String whatsAppGroupLink;

}
