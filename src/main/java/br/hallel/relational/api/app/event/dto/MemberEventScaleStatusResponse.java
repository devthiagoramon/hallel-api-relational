package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MemberEventScaleStatusResponse {
    private String name;
    private MemberEventScaleStatus status;
    private String reasonAbscence;
    public MemberEventScaleStatusResponse toMemberEventScaleStatusResponse(MemberEventScale member) {
        String reason = null;

        if (member.getStatus() == MemberEventScaleStatus.RECUSADO){
            reason = member.getReason_absence();
        }

        return new MemberEventScaleStatusResponse(member.getMemberMinistry().getUser().getName(), member.getStatus(),reason);
    }
}
