package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.MemberEventScaleResponseUserInfos;
import br.hallel.relational.api.app.event.dto.MemberInvitedAndConfirmedResponse;
import br.hallel.relational.api.app.event.dto.MemberNotConfirmedResponse;
import br.hallel.relational.api.app.event.dto.mapper.MemberEventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.exception.ListEventScaleIsEmpty;
import br.hallel.relational.api.app.event.exception.MemberEventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberEventScaleService {

    private final MemberEventScaleRepository memberEventScaleRepository;
    private final UserRepository userRepository;
    private final EventScaleRepository eventScaleRepository;
    private final MemberEventScaleMapper memberEventScaleMapper;

    public MemberEventScaleResponseUserInfos inviteUserIntoScale(
            UUID eventScaleId, UUID userId) {
        log.info("Inviting user {} into scale {}", userId, eventScaleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id %s not found".formatted(userId)));
        EventScale eventScale = eventScaleRepository.findById(eventScaleId)
                .orElseThrow(() -> new EventScaleNotFoundException("Event scale with id %s not found".formatted(eventScaleId)));

        MemberEventScale memberEventScale = memberEventScaleRepository.save(new MemberEventScale(MemberEventScaleStatus.CONVIDADO, null, user, eventScale));

        return memberEventScaleMapper.modelToResponseWithUserInfos(memberEventScale);
    }

    public List<MemberNotConfirmedResponse> listNotConfirmedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(MemberEventScaleStatus.RECUSADO, eventScaleId);

        List<MemberNotConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {
            responseList.add(new MemberNotConfirmedResponse(member.getId(), member.getUser().getName(), member.getReason_absence()));
        }
        return responseList;
    }

    public List<MemberInvitedAndConfirmedResponse> listConfirmedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(MemberEventScaleStatus.PARTICIPANDO, eventScaleId);

        List<MemberInvitedAndConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {
            responseList.add(new MemberInvitedAndConfirmedResponse(member.getId(), member.getUser().getName()));
        }
        return responseList;
    }

    public List<MemberInvitedAndConfirmedResponse> listInvitedMembersEventScale(UUID eventScaleId) {
        List<MemberEventScale> memberStatusList = this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(MemberEventScaleStatus.CONVIDADO, eventScaleId);

        List<MemberInvitedAndConfirmedResponse> responseList = new ArrayList<>();
        for (MemberEventScale member : memberStatusList) {
            responseList.add(new MemberInvitedAndConfirmedResponse(member.getId(), member.getUser().getName()));
        }
        return responseList;
    }

    public MemberNotConfirmedResponse getMemberReasonAbscence(UUID eventScaleId, UUID userId) {
        MemberEventScale memberStatus = this.memberEventScaleRepository.findByStatusAndEventScale_IdAndUser_Id(
                MemberEventScaleStatus.RECUSADO, eventScaleId, userId
        );
        if (memberStatus == null){
            throw new EventScaleNotFoundException("Not found member-not-confirmed with this id! " + userId);
        }
        return new MemberNotConfirmedResponse(memberStatus.getId(), memberStatus.getUser().getName(), memberStatus.getReason_absence());
    }

    public MemberEventScaleResponseUserInfos confirmParticipationUserInEvent(UUID eventScaleId, UUID userId) {
        log.info("Confirming user {} into scale {}", userId, eventScaleId);
        MemberEventScale memberEventScale = this.memberEventScaleRepository.findByUser_IdAndEventScale_Id(eventScaleId, userId).orElseThrow(() -> new MemberEventScaleNotFoundException("User not associated in scale %s".formatted(eventScaleId.toString())));
        memberEventScale.setStatus(MemberEventScaleStatus.PARTICIPANDO);
        MemberEventScale save = this.memberEventScaleRepository.save(memberEventScale);
        return memberEventScaleMapper.modelToResponseWithUserInfos(save);
    }
}
