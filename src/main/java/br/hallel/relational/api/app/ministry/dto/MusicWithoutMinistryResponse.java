package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicWithoutMinistryResponse {
    private UUID id;
    private String name;
    private String description;
    private String letter;
    private String link;
}
