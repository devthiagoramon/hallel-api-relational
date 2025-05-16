package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final MinistryMapper mapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinistryRepository ministryRepository;

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new RuntimeException("Ministry not found"));

        MemberMinistryId id = new MemberMinistryId(userId, ministryId);

        MemberMinistry memberMinistry = new MemberMinistry(id, user, ministry);

        return memberMinistryRepository.save(memberMinistry);
    }

    public void removeMemberFromMinistry(UUID ministryId, UUID userId) {
        log.info("Removing member {} from ministry {}", userId, ministryId);
        MemberMinistry memberMinistry = getMemberMinistryById(ministryId, userId);
        memberMinistryRepository.delete(memberMinistry);
    }

    public List<MinistryResponse> getMinistryThatUserParticipate(UUID userId) {
        log.info("Listing ministries that user participate");
        return mapper.entityMinistriesToResponse(memberMinistryRepository.listMinistryThatUserParticipateByUserId(userId));
    }

}
