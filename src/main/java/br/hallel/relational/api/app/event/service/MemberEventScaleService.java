package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.MemberEventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.exception.MemberEventScaleIllegalArgumentException;
import br.hallel.relational.api.app.event.exception.MemberEventScaleNotFoundException;
import br.hallel.relational.api.app.event.exception.MemberScaleAlreadyHasThatStatus;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.GuestInvitedEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.GuestInvitedEventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberEventScaleService {

    private final MemberEventScaleRepository memberEventScaleRepository;
    private final UserRepository userRepository;
    private final EventScaleRepository eventScaleRepository;
    private final MemberEventScaleMapper memberEventScaleMapper;
    private final GuestInvitedEventScaleRepository guestRepository;

    public MemberEventScaleResponseUserInfos inviteUserIntoScale(
            UUID eventScaleId, UUID userId) {
        log.info("Inviting user {} into scale {}", userId, eventScaleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id %s not found".formatted(userId)));
        EventScale eventScale = eventScaleRepository.findById(eventScaleId)
                .orElseThrow(() -> new EventScaleNotFoundException(
                        "Event scale with id %s not found".formatted(eventScaleId)));

        MemberEventScale memberEventScale = memberEventScaleRepository.save(
                new MemberEventScale(MemberEventScaleStatus.CONVIDADO, null, user, eventScale));

        return memberEventScaleMapper.modelToResponseWithUserInfos(memberEventScale);
    }

    public List<MemberNotConfirmedResponse> listNotConfirmedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(
                MemberEventScaleStatus.RECUSADO, eventScaleId);

        List<MemberNotConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {
            responseList.add(new MemberNotConfirmedResponse(member.getId(), member.getUser(), member.getEventScale().getId(),
                    member.getReason_absence()));
        }
        return responseList;
    }

    public List<MemberInvitedAndConfirmedResponse> listConfirmedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(
                MemberEventScaleStatus.PARTICIPANDO, eventScaleId);

        List<MemberInvitedAndConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {

            responseList.add(new MemberInvitedAndConfirmedResponse(member.getId(), member.getUser().getName(), member.getUser().getEmail(),
                    member.getEventScale().getId()));
        }
        return responseList;
    }

    public List<MemberInvitedAndConfirmedResponse> listInvitedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(
                MemberEventScaleStatus.CONVIDADO, eventScaleId);

        List<MemberInvitedAndConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {

            responseList.add(new MemberInvitedAndConfirmedResponse(member.getId(), member.getUser().getName(), member.getUser().getEmail(),
                    member.getEventScale().getId()));
        }
        log.info("Getting members {} into scale {}", memberStatusList.size(), eventScaleId);
        for (MemberInvitedAndConfirmedResponse m : responseList) {
            System.out.println("Names: " + m.getName());
        }
        return responseList;
    }

    public MemberNotConfirmedResponse getMemberReasonAbscence(UUID eventScaleId, UUID userId) {
        MemberEventScale memberStatus = this.memberEventScaleRepository.findByStatusAndEventScale_IdAndUser_Id(
                MemberEventScaleStatus.RECUSADO, eventScaleId, userId
        );
        if (memberStatus == null) {
            throw new EventScaleNotFoundException("Not found member-not-confirmed with this id! " + userId);
        }
        return new MemberNotConfirmedResponse(memberStatus.getId(), memberStatus.getUser(), memberStatus.getEventScale().getId(),
                memberStatus.getReason_absence());
    }

    public MemberEventScaleResponseUserInfos confirmParticipationUserInEvent(UUID eventScaleId, UUID userId) {
        log.info("Confirming user {} into scale {}", userId, eventScaleId);
        MemberEventScale memberEventScale = this.memberEventScaleRepository
                .findByUser_IdAndEventScale_Id(userId, eventScaleId)
                .orElseThrow(() -> new MemberEventScaleNotFoundException(
                        "User not associated in scale %s".formatted(eventScaleId.toString())));
        memberEventScale.setStatus(MemberEventScaleStatus.PARTICIPANDO);
        if (memberEventScale.getReason_absence() != null) {
            memberEventScale.setReason_absence(null);
        }
        MemberEventScale save = this.memberEventScaleRepository.save(memberEventScale);
        return memberEventScaleMapper.modelToResponseWithUserInfos(save);
    }

    public MemberEventScaleResponseUserInfos declineParticipationUserInEvent(UUID eventScaleId, UUID userId,
                                                                             String reason) {
        log.info("Declining user {} into scale {}", userId, eventScaleId);
        MemberEventScale memberEventScale = this.memberEventScaleRepository
                .findByUser_IdAndEventScale_Id(userId, eventScaleId)
                .orElseThrow(() -> new MemberEventScaleNotFoundException(
                        "User not associated in scale %s".formatted(eventScaleId.toString())));
        memberEventScale.setStatus(MemberEventScaleStatus.RECUSADO);
        memberEventScale.setReason_absence(reason);
        MemberEventScale save = this.memberEventScaleRepository.save(memberEventScale);
        return memberEventScaleMapper.modelToResponseWithUserInfos(save);
    }

    public List<MemberEventScaleResponseUserInfos> listAllMemberEventScaleStatus(UUID idScale) {
        List<MemberEventScaleResponseUserInfos> response = new ArrayList<>();
        this.memberEventScaleRepository.findAllByEventScale_Id(idScale).forEach(member -> {
            response.add(new MemberEventScaleResponseUserInfos(member.getId(), member.getStatus(),
                    member.getReason_absence(), member.getUser()));
        });
        return response;
    }

    public MemberEventScaleResponseUserInfos acceptOrDeclineMember(
            UUID idMemberScale,
            AcceptOrDeclineMemberInScale memberInScale) {
        MemberEventScale member = this.memberEventScaleRepository.findById(idMemberScale).orElseThrow(
                () -> new MemberMinistryRegisterNotFoundException("Member with id" + idMemberScale + " not found!")
        );

        if ((member.getStatus() == MemberEventScaleStatus.RECUSADO && !memberInScale.isAccept())
                || (member.getStatus() == MemberEventScaleStatus.PARTICIPANDO && memberInScale.isAccept())) {
            throw new MemberScaleAlreadyHasThatStatus("Member with id (" + idMemberScale + ") already has status!");
        }
        if (!memberInScale.isAccept() && memberInScale.reason_decline() == null) {
            throw new MemberEventScaleIllegalArgumentException(
                    "Member with id (" + idMemberScale + ") must have a reason to decline the scale!");
        }
        member.setStatus(
                memberInScale.isAccept() ? MemberEventScaleStatus.PARTICIPANDO : MemberEventScaleStatus.RECUSADO);
        member.setReason_absence(memberInScale.isAccept() ? null : memberInScale.reason_decline());

        memberEventScaleRepository.save(member);

        return new MemberEventScaleResponseUserInfos(member.getId(), member.getStatus(), member.getReason_absence(),
                member.getUser());
    }

    public List<EventScale> listAllInvitedScaleOfUserInMinistryInRangeOfDate(UUID userId, UUID ministryId,
                                                                             Date initialDate, Date finalDate) {
        log.info("Listing all invitated scales of user {}", userId);
        return this.memberEventScaleRepository.listAllScaleWhoUserHasBeenInvitedByUserIdAndMinistryIdRangeDate(userId,
                ministryId, initialDate, finalDate);
    }

    public List<EventScaleWithStatusInfos> listAllScaleOfUserInMinistryInRangeOfDateStatus(UUID userId, UUID ministryId, Date initialDate, Date finalDate) {
        log.info("Listing all scales of user {} with status", userId);
        return this.memberEventScaleRepository.listAllScaleWithStatusInfosByUserIdAndMinistryIdRangeDate(userId,
                ministryId, initialDate, finalDate);
    }

    public MemberAuditionStatusResponse getMemberStatus(UUID idmemberministry, UUID idEventScale) {
        Optional<MemberEventScale> member = this.memberEventScaleRepository.findByUser_IdAndEventScale_Id(idmemberministry, idEventScale);

        if (member.isEmpty()) {
            throw new MemberEventScaleNotFoundException("Member with id " + idmemberministry + " not found in scale id: " + idEventScale + " !");
        }

        return new MemberAuditionStatusResponse(member.get().getStatus().name());
    }

    public List<GuestInvitedEventScaleResponse> listAllGuestsInvitedsByEventScaleId(UUID eventScaleId) {
        List<GuestInvitedEventScale> allGuests = this.guestRepository.findAllByEventScale_Id(eventScaleId);
        List<GuestInvitedEventScaleResponse> response = new ArrayList<>();
        for (GuestInvitedEventScale guest : allGuests) {
            response.add(new GuestInvitedEventScaleResponse(
                    guest.getId(), guest.getName(), guest.getEmail(), guest.getPhone(), guest.getEventScale().getId(),
                    guest.getInviteEventScale().getId()
            ));
        }
        return response;
    }

    public List<GuestInvitedEventScaleResponse> listAllGuestsInScaleByID_UserInfo(UUID eventScaleId) {
        List<GuestInvitedEventScale> allGuests = this.guestRepository.findAllByEventScale_Id(eventScaleId);
        List<GuestInvitedEventScaleResponse> response = new ArrayList<>();
        for (GuestInvitedEventScale guest : allGuests) {
            response.add(new GuestInvitedEventScaleResponse(
                    guest.getId(), guest.getName(), guest.getEmail() ));
        }
        return response;
    }
}
