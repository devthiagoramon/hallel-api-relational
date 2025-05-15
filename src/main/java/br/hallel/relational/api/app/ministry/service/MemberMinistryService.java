package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.MemberMinistryResponseWithFunctions;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryMemberRepository;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryRepository;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberMinistryService {

    private final MemberMinistryRepository memberMinistryRepository;
    private final FunctionMinistryMemberRepository functionMinistryMemberRepository;

    private final MinistryMapper mapper;


    public Page<MemberMinistryResponseWithFunctions> getAllMemberOfMinistry(
            UUID ministryId, Pageable pageable) {
        log.info("Listing all members of ministry {}", ministryId);

        Page<User> pageUsers = this.memberMinistryRepository.findAllMembersFromMinistry(ministryId, pageable);
        List<User> users = pageUsers.getContent();

        if (users.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, pageUsers.getTotalElements());
        }

        List<UUID> userIds = users.stream().map(User::getId).toList();
        List<FunctionMinistryMember> functionMinistryMembers = functionMinistryMemberRepository.listAllByUserIds(userIds, ministryId);

        Map<UUID, List<FunctionMinistry>> functionsByUserId = functionMinistryMembers.stream()
                                                                                     .collect(Collectors.groupingBy(
                                                                                             fmm -> fmm.getUser()
                                                                                                       .getId(),
                                                                                             Collectors.mapping(FunctionMinistryMember::getFunctionMinistry, Collectors.toList())
                                                                                                                   ));
        List<MemberMinistryResponseWithFunctions> dtos = users.stream()
                                                              .map(user -> new MemberMinistryResponseWithFunctions(
                                                                      user,
                                                                      functionsByUserId.getOrDefault(user.getId(), Collections.emptyList())
                                                              ))
                                                              .toList();

        return new PageImpl<>(dtos, pageable, pageUsers.getTotalElements());
    }

    public MemberMinistry getMemberMinistryById(UUID ministryId,
                                                UUID userId) {
        Optional<MemberMinistry> memberMinistryId = memberMinistryRepository.findById(new MemberMinistryId(userId, ministryId));
        if (memberMinistryId.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("Member ministry not found");
        }
        return memberMinistryId.get();
    }

    public MemberMinistry addMemberIntoMinistry(UUID ministryId,
                                                UUID userId) {
        log.info("Adding member {} into ministry {}", userId, ministryId);
        MemberMinistryId memberMinistryId = new MemberMinistryId(userId, ministryId);
        return memberMinistryRepository.save(new MemberMinistry(memberMinistryId));
    }

    public void removeMemberFromMinistry(UUID ministryId,
                                         UUID userId) {
        log.info("Removing member {} from ministry {}", userId, ministryId);
        MemberMinistry memberMinistry = getMemberMinistryById(ministryId, userId);
        memberMinistryRepository.delete(memberMinistry);
    }

    public List<MinistryResponse> getMinistryThatUserParticipate(
            UUID userId) {
        log.info("Listing ministries that user participate");
        return mapper.entityMinistriesToResponse(memberMinistryRepository.listMinistryThatUserParticipateByUserId(userId));
    }

}
