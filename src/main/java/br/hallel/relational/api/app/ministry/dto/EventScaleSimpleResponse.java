package br.hallel.relational.api.app.ministry.dto;

import java.util.Date;
import java.util.UUID;

public record EventScaleSimpleResponse
        (
                UUID id,
                Date date
        ){
        public EventScaleSimpleResponse {
                if (date == null) {
                        throw new IllegalArgumentException("Date cannot be null");
                }
        }
}

