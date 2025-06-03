package br.hallel.relational.api.app.ministry.dto;

import java.util.Date;

public record AuditionResponse(String id, String title, String description, Date date, MinistrySimpleResponse ministry,
                               EventScaleSimpleResponse eventScale) {
}
