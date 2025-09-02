package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.dto.AuditionNotConfirmedResponse;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.exception.MemberAuditionMinistryNotFoundException;
import br.hallel.relational.api.app.ministry.model.MemberAuditionMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberAuditionMinistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAuditionMinistryService {
    private MemberAuditionMinistryRepository repository;

    public MemberEventScaleStatus findMemberAuditionByAuditionAndMemberId(UUID auditionId, UUID memberMinistryId) {

        Optional<MemberAuditionMinistry> optional = this.repository.findStatusByAuditionMinistry_IdAndMemberMinistry_Id(auditionId, memberMinistryId);
        if (optional.isEmpty()) {
            return MemberEventScaleStatus.NAO_CONVIDADO;
        }
        log.info("Member status: " + optional.get().getStatus().name());
        return optional.get().getStatus();
    }

    public Boolean confirmInviteAudition(UUID auditionId, UUID memberMinistryId) {

        MemberAuditionMinistry memberAuditionMinistry =
                this.repository.findStatusByAuditionMinistry_IdAndMemberMinistry_Id(auditionId, memberMinistryId).orElseThrow(
                        () -> new MemberAuditionMinistryNotFoundException("member.audition.ministry not found")
                );

        memberAuditionMinistry.setStatus(MemberEventScaleStatus.PARTICIPANDO);

        this.repository.save(memberAuditionMinistry);

        getInfo(memberAuditionMinistry);

        return true;
    }

    private static void getInfo(MemberAuditionMinistry memberAuditionMinistry) {
        log.info("Member Name {} and Status {}: ", memberAuditionMinistry.getMemberMinistry().getUser().getName(),
                memberAuditionMinistry.getStatus().name());
    }

    public Boolean declineInviteAudition(AuditionNotConfirmedResponse requestDTO) {

        MemberAuditionMinistry memberAuditionMinistry =
                this.repository.findStatusByAuditionMinistry_IdAndMemberMinistry_Id(requestDTO.auditionId(), requestDTO.memberMinistryId()).orElseThrow(
                        () -> new MemberAuditionMinistryNotFoundException("member.audition.ministry not found")
                );
        memberAuditionMinistry.setStatus(MemberEventScaleStatus.RECUSADO);
        memberAuditionMinistry.setReason_abscence(requestDTO.reason_abscence());

        this.repository.save(memberAuditionMinistry);

        getInfo(memberAuditionMinistry);
        return true;
    }


}
