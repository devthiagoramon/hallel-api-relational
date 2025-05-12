package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private void deleteCoordinatorFromMemberMinistryTable(UUID userId, UUID ministryId) {
        log.info("Deleting coordinator from member ministry table...");
        Optional<MemberMinistry> optionalMemberMinistry = memberMinistryRepository.findById(new MemberMinistryId(userId, ministryId));
        if (optionalMemberMinistry.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("Member Ministry Id: " + userId + " not found as member ministry!");
        }
        MemberMinistry oldMinistryMember = optionalMemberMinistry.get();
        memberMinistryRepository.delete(oldMinistryMember);
    }

    private void addCoordinatorToMemberMinistryTable(UUID userId, UUID ministryId) {
        log.info("Adding coordinator to member ministry table...");
        MemberMinistryId memberMinistryId = new MemberMinistryId(userId, ministryId);
        memberMinistryRepository.save(new MemberMinistry(memberMinistryId));
    }

}
