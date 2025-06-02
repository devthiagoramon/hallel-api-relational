package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FunctionMinistryResponse {
    private UUID id;
    private UUID ministryId;
    private String name;
    private String description;
    private String icon;
    private String color;
}
