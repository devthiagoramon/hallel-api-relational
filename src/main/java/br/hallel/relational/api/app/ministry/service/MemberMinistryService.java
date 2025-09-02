package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.ministry.dto.MemberMinistryResponseWithFunctions;
import br.hallel.relational.api.app.ministry.dto.MinistryParticipationResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.exception.MinistryNotFoundException;
import br.hallel.relational.api.app.ministry.exception.RoleMinistryNotFoundException;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.*;
import br.hallel.relational.api.app.user.dto.UserShortResponse;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
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


    private UserRepository userRepository;

    private MinistryRepository ministryRepository;

    private FCMSenderService fcmSenderService;

    private RoleMinistryRepository roleMinistryRepository;

    private MinistryMemberRoleRepository ministryMemberRoleRepository;


    public Page<MemberMinistryResponseWithFunctions> getAllMemberOfMinistry(
            UUID ministryId, Pageable pageable) {
        log.info("Listing all members of ministry {}", ministryId);
        try {

            Page<MemberMinistry> pageMemberMinistry = this.memberMinistryRepository.findAllMembersFromMinistry(
                    ministryId, pageable);
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

            Ministry ministry = this.ministryRepository.findById(ministryId).orElseThrow(
                    () -> new MinistryNotFoundException("ministry.id.not.found", ministryId.toString())
            );

            List<MemberMinistryResponseWithFunctions> dtos = membersMinistry.stream()
                    .map(memberMinistry -> {
                        System.out.println("memberMinistry: " + memberMinistry.getUser().getName());
                        StatusParticipationMinistry status = getStatusParticipationInMinistryUser(ministry, memberMinistry.getUser().getId());

                        return new MemberMinistryResponseWithFunctions(
                                memberMinistry.getId(),
                                memberMinistry.getUser(),
                                functionsByUserId.getOrDefault(memberMinistry.getId(), Collections.emptyList()),
                                status
                        );
                    })
                    .toList();

            return new PageImpl<>(dtos, pageable, pageMemberMinistry.getTotalElements());
        } catch (Exception e) {
            log.error("Error listing all members of ministry {}", ministryId, e);
            throw new RuntimeException(e);
        }
    }

    public MemberMinistry getMemberMinistryById(UUID memberMinistryId) {
        Optional<MemberMinistry> memberMinistryOptional = this.memberMinistryRepository.findById(memberMinistryId);
        if (memberMinistryOptional.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("member.ministry.not.found");
        }
        return memberMinistryOptional.get();
    }

    public MemberMinistry getMemberMinistryByUserAndMinistryId(UUID userId, UUID ministryId) {
        Optional<MemberMinistry> memberMinistryOptional = this.memberMinistryRepository.findMemberMinistryByUser_IdAndMinistry_Id(
                userId, ministryId);
        if (memberMinistryOptional.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("member.ministry.not.found");
        }
        return memberMinistryOptional.get();
    }


    @SneakyThrows
    public MemberMinistry addMemberIntoMinistry(UUID ministryId,
                                                UUID userId, RoleMinistryTypes roleMinistry) {
        log.info("Adding member {} into ministry {}", userId, ministryId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user.id.not.found", userId.toString()));

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new MinistryNotFoundException("ministry.id.not.found", ministryId.toString()));

        List<RoleMinistry> rolesMinistryUser = new ArrayList<>(roleMinistryRepository.findAll().stream()
                .filter((roleMinistry1 -> roleMinistry1.getDescription().equals("MEMBER"))).toList());

        if (roleMinistry != RoleMinistryTypes.MEMBER) {
            if (Objects.requireNonNull(roleMinistry) == RoleMinistryTypes.EXTERNAL_COORDINATOR) {
                RoleMinistry externalCoordinatorRole = roleMinistryRepository.findAll().stream()
                        .filter(roleMinistry1 -> roleMinistry1.getDescription().equals("EXTERNAL_COORDINATOR"))
                        .findFirst().orElseThrow(
                                () -> new RoleMinistryNotFoundException("External Coordinator Role not found"));
                rolesMinistryUser.add(externalCoordinatorRole);
            } else {
                throw new RoleNotFoundException("Role ministry not found");
            }
        }
        MemberMinistry memberMinistry = new MemberMinistry(user, ministry);
        MemberMinistry memberMinistrySaved = memberMinistryRepository.save(memberMinistry);

        for (RoleMinistry roleMin : rolesMinistryUser) {
            ministryMemberRoleRepository.save(
                    new MinistryMemberRole(new MinistryMemberRoleIds(memberMinistrySaved.getId(), roleMin.getId())));
        }

        sendNotificationToPersonAddedIntoMinistry(memberMinistry);
        return memberMinistrySaved;
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
                        userId, ministryId)
                .orElseThrow(() -> new MemberMinistryRegisterNotFoundException("member.ministry.not.found"));
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
        List<MemberMinistry> membersMinistries = memberMinistryRepository.findMemberMinistriesByMinistry_Id(ministry.getId());
        for (MemberMinistry memberMinistry : membersMinistries) {
            if (memberMinistry.getUser().getId().equals(userId)) {
                List<String> rolesString = memberMinistry.getMinistryRoles().stream().map(RoleMinistry::getDescription).toList();

                if (rolesString.contains("COORDINATOR")) {
                    System.out.println("COORDINATOR");
                    return StatusParticipationMinistry.COORDINATOR;
                }
                if (rolesString.contains("VICE_COORDINATOR")) {
                    System.out.println("VICE_COORDINATOR");
                    return StatusParticipationMinistry.VICE_COORDINATOR;
                }
                if (rolesString.contains("EXTERNAL_COORDINATOR")) {
                    System.out.println("EXTERNAL_COORDINATOR");
                    return StatusParticipationMinistry.EXTERNAL_COORDINATOR;
                }
            }
        }
        return StatusParticipationMinistry.MEMBER;
    }

    public Page<UserShortResponse> getUsersAddableInMinistry(UUID ministryId, Pageable pageable) {
        log.info("Listing members to add in ministry...");
        return this.userRepository.listUsersAddableInMinistry(ministryId, pageable);
    }
}
