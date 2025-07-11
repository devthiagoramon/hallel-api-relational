package br.hallel.relational.api.app.event.dto;

import java.util.List;

public record EventScaleReportPDF(
        String title,
        String ministry_name,
        String text_interval,
        List<String> participants,
        List<String> invited,
        List<String> decline,
        String coordinator_name,
        String vice_coodinator_name,
        List<String> events_title,
        List<String> date_events
) {
}
