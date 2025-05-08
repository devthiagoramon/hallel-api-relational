package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventHomePageResponse {
    private UUID id;
    private String title;
    private String description;
    private Date date;
    private String banner;
    private String fileImageUrl;
    private Boolean isImportant;
    private String date_time;
    
    public EventHomePageResponse toEventResponse(Event response){
        EventHomePageResponse e = new EventHomePageResponse();
        e.setId(response.getId());
        e.setTitle(response.getTitle());
        e.setDescription(response.getDescription());
        e.setDate(response.getDate());
        e.setIsImportant(response.getIsImportant());
        e.setDate_time(response.getDate_time());
        return e;
    }
}
