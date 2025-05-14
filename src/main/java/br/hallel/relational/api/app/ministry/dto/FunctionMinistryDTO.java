package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionMinistryDTO {
    private String name;
    private String description;
    private String icon;
    private String color;
}
