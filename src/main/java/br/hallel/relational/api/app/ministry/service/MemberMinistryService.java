package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.ministry.dto.MemberMinistryResponseWithFunctions;
import br.hallel.relational.api.app.ministry.dto.MinistryParticipationResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryMemberRepository;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import br.hallel.relational.api.app.user.dto.UserShortResponse;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinistryRepository ministryRepository;
    @Autowired
    private FCMSenderService fcmSenderService;


    public Page<MemberMinistryResponseWithFunctions> getAllMemberOfMinistry(
            UUID ministryId, Pageable pageable) {
        log.info("Listing all members of ministry {}", ministryId);

        Page<MemberMinistry> pageMemberMinistry = this.memberMinistryRepository.findAllMembersFromMinistry(ministryId, pageable);
        List<MemberMinistry> membersMinistry = pageMemberMinistry.getContent();

        if (membersMinistry.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, pageMemberMinistry.getTotalElements());
        }

        List<UUID> memberMinistryIds = membersMinistry.stream().map(MemberMinistry::getId).toList();
        List<FunctionMinistryMember> functionMinistryMembers = functionMinistryMemberRepository.listAllByMemberMinistryIds(
                memberMinistryIds, ministryId);

        Map<UUID, List<FunctionMinistry>> functionsByUserId = functionMinistryMembers.stream()
                .collect(Collectors.groupingBy(
                        fmm -> fmm.getMemberMinistry()
                                .getId(),
                        Collectors.mapping(FunctionMinistryMember::getFunctionMinistry, Collectors.toList())
                ));

        List<MemberMinistryResponseWithFunctions> dtos = membersMinistry.stream()
                .map(memberMinistry -> new MemberMinistryResponseWithFunctions(
                        memberMinistry.getId(),
                        memberMinistry.getUser(),
                        functionsByUserId.getOrDefault(memberMinistry.getId(), Collections.emptyList())
                ))
                .toList();

        return new PageImpl<>(dtos, pageable, pageMemberMinistry.getTotalElements());
    }

    public MemberMinistry getMemberMinistryById(UUID memberMinistryId) {
        Optional<MemberMinistry> memberMinistryOptional = this.memberMinistryRepository.findById(memberMinistryId);
        if (memberMinistryOptional.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("Member ministry not found");
        }
        return memberMinistryOptional.get();
    }


    public MemberMinistry addMemberIntoMinistry(UUID ministryId,
                                                UUID userId) {
        log.info("Adding member {} into ministry {}", userId, ministryId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new RuntimeException("Ministry not found"));

        MemberMinistry memberMinistry = new MemberMinistry(user, ministry);
        sendNotificationToPersonAddedIntoMinistry(memberMinistry);
        return memberMinistryRepository.save(memberMinistry);
    }

    private void sendNotificationToPersonAddedIntoMinistry(MemberMinistry memberMinistry) {
        List<DeviceNotification> devicesCoordinador = memberMinistry.getUser().getDevicesUser();

        devicesCoordinador.forEach(device -> {
            fcmSenderService.sendNotification(device.getFcmToken(),
                    "Ministério %s".formatted(memberMinistry.getMinistry().getTitle()),
                    "Parabéns! Você foi adicionado pelo coordenador ao ministério %s, veja agora.".formatted(
                            memberMinistry.getMinistry().getTitle()),
                    personAddedNotificationTemplate(memberMinistry));
        });
    }

    private Map<String, String> personAddedNotificationTemplate(MemberMinistry memberMinistry) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "add_member_ministry");
        map.put("action", "open_panel");
        map.put("userId", memberMinistry.getUser().getId().toString());
        map.put("ministryId", memberMinistry.getMinistry().getId().toString());
        return map;
    }


    public void removeMemberFromMinistry(UUID ministryId,
                                         UUID userId) {
        log.info("Removing member {} from ministry {}", userId, ministryId);
        MemberMinistry memberMinistry = this.memberMinistryRepository.findMemberMinistryByUser_IdAndMinistry_Id(
                userId, ministryId).orElseThrow(()->new MemberMinistryRegisterNotFoundException("Member ministry not found"));
        memberMinistryRepository.delete(memberMinistry);
    }

    public List<MinistryParticipationResponse> getMinistryThatUserParticipate(
            UUID userId) {
        log.info("Listing ministries that user participate");
        List<Ministry> ministries = memberMinistryRepository.listMinistryThatUserParticipateByUserId(userId);

        return ministries.stream()
                .map(ministry -> new MinistryParticipationResponse(ministry.getId(), ministry.getTitle(),
                        ministry.getImage(), getStatusParticipationInMinistryUser(ministry, userId))).toList();
    }

    private StatusParticipationMinistry getStatusParticipationInMinistryUser(Ministry ministry, UUID userId) {
        if (ministry.getCoordinator().getId().equals(userId)) {
            return StatusParticipationMinistry.COORDINATOR;
        }
        if (ministry.getViceCoordinator().getId().equals(userId)) {
            return StatusParticipationMinistry.VICE_COORDINATOR;
        }
        return StatusParticipationMinistry.MEMBER;
    }

    public Page<UserShortResponse> getUsersAddableInMinistry(UUID ministryId, Pageable pageable) {
        log.info("Listing members to add in ministry...");
        return this.userRepository.listUsersAddableInMinistry(ministryId, pageable);
    }
}
