package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.MinistryType;

import java.util.UUID;

public record MinistrySimpleResponse(
        UUID id,
        String title,
        String image,
        MinistryType type
) {

    public MinistrySimpleResponse(UUID id, String title, String image, MinistryType type) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.type = type;
    }
}
