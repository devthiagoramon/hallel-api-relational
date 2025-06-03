package br.hallel.relational.api.app.ministry.dto;

import java.util.UUID;

public record MinistrySimpleResponse(
        UUID id,
        String title,
        String image
) {

    public MinistrySimpleResponse(UUID id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }
}
