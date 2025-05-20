package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class MemberNotConfirmedResponse {
    private UUID id;
    private String name;
    private String reasonAbscence;
}
