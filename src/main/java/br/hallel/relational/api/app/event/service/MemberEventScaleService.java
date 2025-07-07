package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.MemberEventScaleMapper;
import br.hallel.relational.api.app.event.exception.*;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.GuestInvitedEventScaleRepository;
import br.hallel.relational.api.app.event.repository.InviteEventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.dto.RepertoryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.RepertoryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberEventScaleService {

    private final MemberEventScaleRepository memberEventScaleRepository;
    private final UserRepository userRepository;
    private final EventScaleRepository eventScaleRepository;
    private final MemberEventScaleMapper memberEventScaleMapper;
    private final GuestInvitedEventScaleRepository guestRepository;
    private final InviteEventScaleRepository inviteRepository;
    private final MemberMinistryRepository memberMinistryRepository;
    private final FCMSenderService fcmSenderService;

    private final RepertoryMapper repertoryMapper;

    public EventScaleSimpleResponse inviteUserIntoScale(
            UUID eventScaleId, List<UUID> memberMinistryIds) {

        log.info("Inviting users {} into scale {}", memberMinistryIds, eventScaleId);
        EventScale eventScale = eventScaleRepository.findById(eventScaleId)
                .orElseThrow(() -> new EventScaleNotFoundException(
                        "Event scale with id %s not found".formatted(eventScaleId)));
        List<MemberMinistry> memberMinistries = memberMinistryRepository.findAllById(memberMinistryIds);
        if (memberMinistries.size() != memberMinistryIds.size()) {
            throw new UserNotFoundException("Um ou mais usuários não foram encontrados.");
        }
        List<MemberEventScale> invitedMembers = new ArrayList<>();
        for (MemberMinistry memberMinistry : memberMinistries) {
            MemberEventScale member = new MemberEventScale(
                    MemberEventScaleStatus.CONVIDADO,
                    null, memberMinistry, eventScale
            );
            sendNotificationInviteIntoScale(eventScale, memberMinistry.getUser());
            invitedMembers.add(member);
        }
        memberEventScaleRepository.saveAll(invitedMembers);
        return new EventScaleSimpleResponse(eventScaleId, eventScale.getDate());
    }

    private void sendNotificationInviteIntoScale(EventScale eventScale, User user) {
        Hibernate.initialize(user.getDevicesUser());
        List<DeviceNotification> deviceNotifications = user.getDevicesUser();
        if (deviceNotifications.isEmpty()) {
            return;
        }
        deviceNotifications.forEach(deviceNotification -> {
            fcmSenderService.sendNotification(
                    deviceNotification.getFcmToken(),
                    "Convite para escala do ministério",
                    "Você foi convidado para participar da escala do ministério %s, veja agora e confirme a sua participação!".formatted(eventScale.getMinistry().getTitle()),
                    dataNotificationInviteIntoScale(eventScale, user)
                    );
        });

    }

    private Map<String, String> dataNotificationInviteIntoScale(EventScale eventScale, User user) {
        Map<String, String> data = new HashMap<>();
        data.put("userId", user.getId().toString());
        data.put("eventScaleId", eventScale.getId().toString());
        data.put("ministryId", eventScale.getMinistry().getId().toString());
        data.put("type", "invite_scale");
        data.put("action", "panel_ministry");
        data.put("dateScale", eventScale.getDate().toString());
        return data;
    }


    public boolean viewInvite(UUID eventScaleId, UUID userId) {

        this.eventScaleRepository.findById(eventScaleId).orElseThrow(
                () -> new EventScaleNotFoundException("Event scale with id %s not found".formatted(eventScaleId))
        );

        log.info("Viewing invite user {} into scale {}", userId, eventScaleId);
        Optional<MemberEventScale> optional = memberEventScaleRepository.findByMemberMinistry_IdAndEventScale_Id(userId,
                eventScaleId);

        if (optional.isEmpty() || optional.get().getDate_view() != null) {
            log.info("Member already view the invite or member not invited to event Scale");
            return false;
        }

        MemberEventScale member = optional.get();
        member.setDate_view(new Date());
        this.memberEventScaleRepository.save(member);
        return true;
    }

    @Transactional
    public EventScaleSimpleResponse withdrawInvitation(
            UUID eventScaleId, List<UUID> userId) {
        Date date = null;

        for (UUID id : userId) {
            System.out.println("User id: " + id);
            date = this.eventScaleRepository.findById(eventScaleId).get().getDate();
            this.memberEventScaleRepository.deleteMemberEventScaleByEventScale_IdAndMemberMinistry_Id(eventScaleId, id);
        }

        log.info("Withdraw Invitation user {} into scale {}", userId, eventScaleId);
        return new EventScaleSimpleResponse(eventScaleId, date);
    }

    public List<MemberNotConfirmedResponse> listNotConfirmedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(
                MemberEventScaleStatus.RECUSADO, eventScaleId);

        List<MemberNotConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {
            responseList.add(new MemberNotConfirmedResponse(member.getId(), member.getMemberMinistry().getUser(), member.getEventScale().getId(),
                    member.getReason_absence()));
        }
        return responseList;
    }

    public List<MemberInvitedAndConfirmedResponse> listConfirmedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(
                MemberEventScaleStatus.PARTICIPANDO, eventScaleId);

        List<MemberInvitedAndConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {

            responseList.add(new MemberInvitedAndConfirmedResponse(member.getId(), member.getMemberMinistry().getUser().getName(), member.getMemberMinistry().getUser().getEmail(),
                    member.getEventScale().getId()));
        }
        return responseList;
    }

    public List<MemberInvitedAndConfirmedResponse> listInvitedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(
                MemberEventScaleStatus.CONVIDADO, eventScaleId);

        List<MemberInvitedAndConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {
            responseList.add(new MemberInvitedAndConfirmedResponse(member.getId(), member.getMemberMinistry().getUser().getName(), member.getMemberMinistry().getUser().getEmail(),
                    member.getEventScale().getId()));
        }
        log.info("Getting members {} into scale {}", memberStatusList.size(), eventScaleId);
        for (MemberInvitedAndConfirmedResponse m : responseList) {
            System.out.println("Names: " + m.getName());
        }
        return responseList;
    }

    public MemberNotConfirmedResponse getMemberReasonAbscence(UUID eventScaleId, UUID userId) {
        MemberEventScale memberStatus = this.memberEventScaleRepository.findByStatusAndEventScale_IdAndMemberMinistry_Id(
                MemberEventScaleStatus.RECUSADO, eventScaleId, userId
        );
        if (memberStatus == null) {
            throw new EventScaleNotFoundException("Not found member-not-confirmed with this id! " + userId);
        }
        return new MemberNotConfirmedResponse(memberStatus.getId(), memberStatus.getMemberMinistry().getUser(), memberStatus.getEventScale().getId(),
                memberStatus.getReason_absence());
    }

    public MemberEventScaleResponseUserInfos confirmParticipationUserInEvent(UUID eventScaleId, UUID userId) {
        log.info("Confirming user {} into scale {}", userId, eventScaleId);
        MemberEventScale memberEventScale = this.memberEventScaleRepository
                .findByMemberMinistry_IdAndEventScale_Id(userId, eventScaleId)
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
                .findByMemberMinistry_IdAndEventScale_Id(userId, eventScaleId)
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
                    member.getReason_absence(), member.getMemberMinistry().getUser()));
        });
        return response;
    }

    public MemberEventScaleResponseUserInfos acceptOrDeclineMember(
            UUID idMemberScale, UUID eventScaleId, AcceptOrDeclineMemberInScale memberInScale) {
        MemberEventScale member = this.memberEventScaleRepository.findByMemberMinistry_IdAndEventScale_Id(idMemberScale, eventScaleId).orElseThrow(
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
                member.getMemberMinistry().getUser());
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

    public List<EventScaleWithMembers>
    listAllEventScaleWithMembers(UUID userId, UUID ministryId, Date initialDate, Date finalDate) {
        log.info("Listing all scales of user {} with status", userId);

        List<EventScaleWithMembers> escalasPrincipais =
                this.memberEventScaleRepository.listAllEventScaleWithMembers(
                        userId, ministryId, initialDate, finalDate
                );


        for (EventScaleWithMembers escala : escalasPrincipais) {
            UUID escalaId = escala.getScaleId();

            List<MemberEventScale> allMembers = memberEventScaleRepository.findAllByEventScaleId((escalaId));

            escala.setMembersParticipate(allMembers.stream()
                    .filter(m -> m.getStatus() == MemberEventScaleStatus.PARTICIPANDO)
                    .map(m -> m.getMemberMinistry().getUser().getName())
                    .toList());

            escala.setMembersDecline(allMembers.stream()
                    .filter(m -> m.getStatus() == MemberEventScaleStatus.RECUSADO)
                    .map(m -> m.getMemberMinistry().getUser().getName())
                    .toList());

            escala.setMembersInvited(allMembers.stream()
                    .filter(m -> m.getStatus() == MemberEventScaleStatus.CONVIDADO)
                    .map(m -> m.getMemberMinistry().getUser().getName())
                    .toList());
        }


        return escalasPrincipais;
    }

    public MemberAuditionStatusResponse getMemberStatus(UUID idmemberministry, UUID idEventScale) {
        Optional<MemberEventScale> member = this.memberEventScaleRepository.findByMemberMinistry_IdAndEventScale_Id(idmemberministry, idEventScale);

        if (member.isEmpty()) {
            throw new MemberEventScaleNotFoundException("Member with id " + idmemberministry + " not found in scale id: " + idEventScale + " !");
        }

        return new MemberAuditionStatusResponse(member.get().getStatus().name());
    }

    //----- GUESTS SERVICES -----

    public GuestInvitedEventScaleResponse createGuestInvitedEventScale(GuestInvitedEventScaleDTO dto) {

        EventScale eventScale = this.eventScaleRepository.findById(dto.eventScaleId()).orElseThrow(
                () -> new EventScaleNotFoundException("Event scale with id " + dto.eventScaleId() + " not found!")
        );
        InviteEventScale invite = null;
        if (dto.inviteEventScaleId() != null) {
            invite = this.inviteRepository.findById(dto.inviteEventScaleId()).get();
        } else {
            invite = this.inviteRepository.save(
                    new InviteEventScale(true, dto.message(), new Date(), null)
            );
        }

        //        boolean responseSendMessage = telegramService.sendMessageWithEventToContact(dto.getTelefone(), dto.getMensagem(), eventoEscala);

        GuestInvitedEventScale save = this.guestRepository.save(
                new GuestInvitedEventScale(
                        dto.name(), dto.email(), dto.phone(), eventScale, invite
                )
        );
        return new GuestInvitedEventScaleResponse(
                save.getId(), save.getName(), save.getEmail(), save.getPhone(), save.getEventScale().getId(),
                save.getInviteEventScale().getId()
        );
    }


    public GuestInvitedEventScaleResponse getGuestInvitedEventScale(UUID guestId) {
        GuestInvitedEventScale guest = this.guestRepository.findById(guestId).orElseThrow(() -> new InviteEventScaleException("Guest with id " + guestId + " not found!"));
        return new GuestInvitedEventScaleResponse(
                guestId, guest.getName(), guest.getEmail(), guest.getPhone(), guest.getEventScale().getId(),
                guest.getInviteEventScale().getId()
        );
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

    public GuestInvitedEventScaleResponse editGuestInvited(UUID guestId, GuestInvitedEventScaleDTO dto) {
        GuestInvitedEventScale guest = this.guestRepository.findById(guestId).orElseThrow(() -> new InviteEventScaleException("Guest with id " + guestId + " not found!"));
        guest.setName(dto.name());
        guest.setEmail(dto.email());
        guest.setPhone(dto.phone());

        this.guestRepository.save(guest);
        return new GuestInvitedEventScaleResponse(guest.getId(), guest.getName(), guest.getEmail(), guest.getPhone(), guest.getEventScale().getId(), guest.getInviteEventScale().getId());
    }

    public Boolean removeGuestFromEventScaleById(UUID guestId) {
        Optional<GuestInvitedEventScale> optional = this.guestRepository.findById(guestId);
        if (optional.isEmpty()) {
            log.info("Guest with id " + guestId + " not found in scale id: " + guestId);
            return false;
        }

        GuestInvitedEventScale guest = optional.get();

        if (guest.getInviteEventScale() != null) {
            UUID inviteId = guest.getInviteEventScale().getId();
            log.info("Removing invite ID: " + inviteId + " related to guest: " + guestId);
            this.inviteRepository.deleteById(inviteId);
        } else {
            log.warn("Guest " + guestId + " does not have an associated invite.");
            return false;
        }

        this.guestRepository.deleteById(guestId);

        log.info("Guest with id " + guestId + " removed from scale.");
        return true;
    }

    public InviteEventScaleResponse getInvitesInEventScaleId(UUID inviteId) {
        InviteEventScale response = this.inviteRepository.findById(inviteId).orElseThrow(
                () -> new InviteEventScaleException("Invite with id " + inviteId + " not found in scale id: " + inviteId)
        );

        return new InviteEventScaleResponse(
                response.getId(), response.isSent(), response.getMessage(), response.getDateSend(), response.getDateEdit()
        );
    }

    public InviteEventScaleResponse
    editInvitesInEventScaleId(UUID inviteId, UUID guestId, String message) {
        InviteEventScale invite = this.inviteRepository.findById(inviteId).orElseThrow(
                () -> new InviteEventScaleException("Invite with id " + inviteId + " not found in scale id: " + inviteId)
        );
        GuestInvitedEventScale guest =
                this.guestRepository.findById(guestId).orElseThrow(() -> new InviteEventScaleException("Guest with id " + guestId + " not found in scale"));
        invite.setMessage(message);
        invite.setDateEdit(new Date());
        guest.setInviteEventScale(invite);
//        boolean messageSended = telegramService.sendMessageToContact(convidadoEscalaMinisterio.getTelefone(), conviteEscalaOld.getMensagem());

        this.inviteRepository.save(invite);
        this.guestRepository.save(guest);

        return new InviteEventScaleResponse(
                invite.getId(), invite.isSent(), invite.getMessage(), invite.getDateSend(), invite.getDateEdit()
        );
    }

    public List<GuestInvitedEventScaleResponse> listAllGuestsInScaleByID_UserInfo(UUID eventScaleId) {
        List<GuestInvitedEventScale> allGuests = this.guestRepository.findAllByEventScale_Id(eventScaleId);
        List<GuestInvitedEventScaleResponse> response = new ArrayList<>();
        for (GuestInvitedEventScale guest : allGuests) {
            response.add(new GuestInvitedEventScaleResponse(
                    guest.getId(), guest.getName(), guest.getEmail()));
        }
        return response;
    }

    public List<RepertoryResponse> listAllRepertoryOfEventScale(UUID eventScaleId) {
        return this.repertoryMapper.toListResponseRepertory(this.eventScaleRepository.findRepertoriesOfEventScale(eventScaleId));
    }
}
