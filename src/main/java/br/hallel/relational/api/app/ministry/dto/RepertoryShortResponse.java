package br.hallel.relational.api.app.ministry.dto;

import java.util.UUID;

public record RepertoryShortResponse(
        UUID id, String name, String description, UUID ministryID
) {
}
