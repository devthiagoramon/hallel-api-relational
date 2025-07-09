package br.hallel.relational.api.app.event.dto;


import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.user.model.User;

import java.util.UUID;

public record MemberEventScaleResponseUserInfos(UUID id, MemberEventScaleStatus status, String reason_absence, MemberMinistry memberMinistry) {
}
