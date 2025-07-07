package br.hallel.relational.api.app.event.dto;

import java.util.Date;
import java.util.UUID;

public record EventSimpleResponse(
        UUID id, String title, Date date
) {
}
