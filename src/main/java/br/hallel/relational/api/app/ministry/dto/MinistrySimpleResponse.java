package br.hallel.relational.api.app.ministry.dto;

import java.util.UUID;

public record MinistrySimpleResponse(
        UUID id,
        String title,
        String image
) {}
