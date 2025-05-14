package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberMinistryService {

    private final MemberMinistryRepository memberMinistryRepository;


    public Page<User> getAllMemberOfMinistry(UUID ministryId, Pageable pageable) {
        log.info("Listing all members of ministry {}", ministryId);
        return this.memberMinistryRepository.findAllMembersFromMinistry(ministryId, pageable);
    }

    public MemberMinistry getMemberMinistryById(UUID ministryId, UUID userId) {
        Optional<MemberMinistry> memberMinistryId = memberMinistryRepository.findById(new MemberMinistryId(userId, ministryId));
        if (memberMinistryId.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("Member ministry not found");
        }
        return memberMinistryId.get();
    }

    public MemberMinistry addMemberIntoMinistry(UUID ministryId, UUID userId) {
        log.info("Adding member {} into ministry {}", userId, ministryId);
        MemberMinistryId memberMinistryId = new MemberMinistryId(userId, ministryId);
        return memberMinistryRepository.save(new MemberMinistry(memberMinistryId));
    }

    public void removeMemberFromMinistry(UUID ministryId, UUID userId) {
        log.info("Removing member {} from ministry {}", userId, ministryId);
        MemberMinistry memberMinistry = getMemberMinistryById(ministryId, userId);
        memberMinistryRepository.delete(memberMinistry);
    }

    public List<Ministry> getMinistryThatUserParticipate(UUID userId){
        log.info("Listing ministries that user participate");
        return memberMinistryRepository.listMinistryThatUserParticipateByUserId(userId);
    }

}
