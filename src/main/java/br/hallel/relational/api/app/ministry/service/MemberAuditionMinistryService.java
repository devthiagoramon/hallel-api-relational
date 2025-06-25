package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.dto.AuditionNotConfirmedResponse;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.exception.MemberAuditionMinistryNotFound;
import br.hallel.relational.api.app.ministry.model.MemberAuditionMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberAuditionMinistryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MemberAuditionMinistryService {
    @Autowired
    private MemberAuditionMinistryRepository repository;

    public MemberEventScaleStatus findMemberAuditionByAuditionAndMemberId(UUID auditionId, UUID memberId) {

        Optional<MemberAuditionMinistry> optional = this.repository.findStatusByAuditionMinistry_IdAndUser_Id(auditionId, memberId);
        if (optional.isEmpty()) {
            return MemberEventScaleStatus.NAO_CONVIDADO;
        }
        log.info("Member status: " + optional.get().getStatus().name());
        return optional.get().getStatus();
    }

    public Boolean confirmInviteAudition(UUID auditionId, UUID memberId) {

        MemberAuditionMinistry memberAuditionMinistry =
                this.repository.findStatusByAuditionMinistry_IdAndUser_Id(auditionId, memberId).orElseThrow(
                        () -> new MemberAuditionMinistryNotFound("Member in Audition not found")
                );

        memberAuditionMinistry.setStatus(MemberEventScaleStatus.PARTICIPANDO);

        this.repository.save(memberAuditionMinistry);

        log.info("Member Name {} and Status {}: ", memberAuditionMinistry.getUser().getName(),
                memberAuditionMinistry.getStatus().name());

        return true;
    }

    public Boolean declineInviteAudition(AuditionNotConfirmedResponse requestDTO) {

        MemberAuditionMinistry memberAuditionMinistry =
                this.repository.findStatusByAuditionMinistry_IdAndUser_Id(requestDTO.auditionId(), requestDTO.userId()).orElseThrow(
                        () -> new MemberAuditionMinistryNotFound("Member in Audition not found")
                );
        memberAuditionMinistry.setStatus(MemberEventScaleStatus.RECUSADO);
        memberAuditionMinistry.setReason_abscence(requestDTO.reason_abscence());

        this.repository.save(memberAuditionMinistry);

        log.info("Member Name {} and Status {}: ", memberAuditionMinistry.getUser().getName(),
                memberAuditionMinistry.getStatus().name());
        return true;
    }


}
