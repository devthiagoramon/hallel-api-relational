package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventShortResponse {
    private String id;
    private String title;
    private Date date;
    private String fileImageUrl;
    private String banner;
}