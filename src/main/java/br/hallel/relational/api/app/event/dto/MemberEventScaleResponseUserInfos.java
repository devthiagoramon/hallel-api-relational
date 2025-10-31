package br.hallel.relational.api.app.event.dto;


import br.hallel.relational.api.app.event.model.enum_type.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;

import java.util.UUID;

public record MemberEventScaleResponseUserInfos(UUID id, MemberEventScaleStatus status, String reason_absence, MemberMinistry memberMinistry) {
}
