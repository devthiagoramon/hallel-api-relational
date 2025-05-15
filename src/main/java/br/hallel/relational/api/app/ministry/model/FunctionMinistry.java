package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "function_ministry")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FunctionMinistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "ministry_id")
    private UUID ministryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String color;
}
