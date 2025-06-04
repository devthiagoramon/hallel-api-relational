package br.hallel.relational.api.app.ministry.dto;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter @ToString
public class AuditionDTO {
    private String title;
    private String description;
    private Date date;
    private UUID ministry;
    private UUID eventScale;
}
