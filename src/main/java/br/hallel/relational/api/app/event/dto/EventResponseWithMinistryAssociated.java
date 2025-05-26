package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String banner_url;
    private String image_url;
    private Boolean isImportant;
    private String local_event_name;
    private Double local_event_longitude;
    private Double local_event_latitude;
    private Double value;
    private List<MinistryResponse> ministries;

    public EventResponseWithMinistryAssociated(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.date = event.getDate();
        this.banner_url = event.getBanner_url();
        this.image_url = event.getImage_url();
        this.isImportant = event.getIsImportant();
        this.local_event_name = event.getLocal_event_name();
        this.local_event_longitude = event.getLocal_event_longitude();
        this.local_event_latitude = event.getLocal_event_latitude();
        this.value = event.getValue();



        if (event.getScales() != null) {
            for (EventScale scale : event.getScales()) {
                System.out.println(scale.getId());
            }
            this.ministries = event.getScales().stream()
                    .map(EventScale::getMinistry)
                    .filter(Objects::nonNull)
                    .map(ministryEntity -> new MinistryResponse(
                            ministryEntity.getId(),
                            ministryEntity.getTitle(),
                            ministryEntity.getDescription(),
                            ministryEntity.getImage(),
                            ministryEntity.getHasRepertoire(),
                            ministryEntity.getMinistryType(),
                            ministryEntity.getCoordinatorId(),
                            ministryEntity.getViceCoordinatorId()
                    ))
                    .collect(Collectors.toList());
        } else {
            this.ministries = new ArrayList<>();
        }
    }

    // Se você ainda precisar do @AllArgsConstructor para outros usos:
    public EventResponseWithMinistryAssociated(UUID id, String title, String description, Date date,
                                               String banner_url, String image_url, Boolean isImportant,
                                               String local_event_name, Double local_event_longitude,
                                               Double local_event_latitude, Double value,
                                               List<MinistryResponse> ministries) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.banner_url = banner_url;
        this.image_url = image_url;
        this.isImportant = isImportant;
        this.local_event_name = local_event_name;
        this.local_event_longitude = local_event_longitude;
        this.local_event_latitude = local_event_latitude;
        this.value = value;
        this.ministries = ministries;
    }

}
