package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.exception.MemberAuditionMinistryNotFound;
import br.hallel.relational.api.app.ministry.model.MemberAuditionMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberAuditionMinistryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class MemberAuditionMinistryService {
    @Autowired
    private MemberAuditionMinistryRepository repository;

    public MemberEventScaleStatus findMemberAuditionByAuditionAndMemberId(UUID auditionId,
                                                                          UUID memberId) {

        MemberAuditionMinistry memberAuditionMinistry =
                this.repository.findStatusByAuditionMinistry_IdAndUser_Id(auditionId, memberId).orElseThrow(
                        () -> new MemberAuditionMinistryNotFound("Member in Audition not found")
                );
        log.info("Member status: "+memberAuditionMinistry.getStatus().name());
        return memberAuditionMinistry.getStatus();
    }
}
