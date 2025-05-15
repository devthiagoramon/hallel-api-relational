package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MusicAddEditDTO {
    private String name;
    private String description;
    private String letter;
    private String link;
    private UUID ministry;
}
