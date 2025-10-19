package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.exception.EventParticipationException;
import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.ministry.dto.ScaleChatInfoResponse;
import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse;
import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantUserResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.repository.ScaleChatParticipantRepository;
import br.hallel.relational.api.app.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScaleChatParticipantService {

    private final ScaleChatParticipantRepository scaleChatParticipantRepository;
    private final EventScaleRepository eventScaleRepository;
    private final MemberEventScaleRepository memberEventScaleRepository;

    public List<ScaleChatParticipantResponse> createScaleChat(UUID scaleId) {
        log.info("Creating scale chat of scale {}", scaleId);
        EventScale eventScale = eventScaleRepository.findById(scaleId)
                .orElseThrow(() -> new EventScaleNotFoundException("event.scale.not.found", scaleId.toString()));
        List<MemberEventScale> membersScale = memberEventScaleRepository.findAllByEventScale_Id(scaleId);
        for (MemberEventScale memberEventScale : membersScale) {
            scaleChatParticipantRepository.save(
                    new ScaleChatParticipant(eventScale, memberEventScale));
        }
        return this.scaleChatParticipantRepository.listParticipantsWithUserModelByScaleId(scaleId);
    }

    public void deleteScaleChat(UUID scaleId) {
        log.info("Deleting scale chat of scale {}", scaleId);
        List<ScaleChatParticipant> participants = scaleChatParticipantRepository.findScaleChatParticipantsByEventScale_Id(
                scaleId);
        scaleChatParticipantRepository.deleteAll(participants);
    }

    public List<ScaleChatParticipantUserResponse> listParticipantsScaleChat(UUID scaleId) {
        log.info("Listing participants scale chat of scale {}", scaleId);
        List<ScaleChatParticipant> participantsChat = scaleChatParticipantRepository.listParticipantsOfScale(
                scaleId);
        List<ScaleChatParticipantUserResponse> response = new ArrayList<>();
        for (ScaleChatParticipant scaleChatParticipant : participantsChat) {
            response.add(new ScaleChatParticipantUserResponse(
                    scaleChatParticipant.getId(),
                    scaleChatParticipant.getMemberEventScale().getMemberMinistry().getUser()
            ));
        }
        return response;
    }


    public Boolean verifyIfScaleChatExists(UUID scaleId) {
        log.info("Verifying scale chat of scale {} existence", scaleId);
        List<ScaleChatParticipant> participants = scaleChatParticipantRepository.findScaleChatParticipantsByEventScale_Id(
                scaleId);
        return !participants.isEmpty();
    }

    public ScaleChatInfoResponse getInfoOfScaleChat(UUID scaleId) {
        log.info("Listing info scale chat of scale {}", scaleId);
        List<ScaleChatParticipant> participantsChat = scaleChatParticipantRepository.listParticipantsOfScale(
                scaleId);
        List<User> participants = participantsChat.stream()
                .map(participant -> participant.getMemberEventScale().getMemberMinistry().getUser()).toList();
        EventShortResponse eventShortResponse = eventScaleRepository.findScaleByIdWithInfos(scaleId).getEvento();
        return new ScaleChatInfoResponse(scaleId, eventShortResponse, participants);
    }

    public void removeParticipantFromScaleChat(UUID scaleChatParticipantId) {
        log.info("Removing participants scale chat of scale {}", scaleChatParticipantId);
        ScaleChatParticipant scaleChatParticipant = this.scaleChatParticipantRepository.findById(scaleChatParticipantId)
                .orElseThrow(() -> new EventParticipationException(
                        "Participante não encontrado pelo id: %s".formatted(scaleChatParticipantId.toString())));
        this.scaleChatParticipantRepository.delete(scaleChatParticipant);
    }

    public ScaleChatParticipant addParticipantFromScaleChatParticipant(UUID userId, UUID scaleId) {
        log.info("Add participant for scale chat of scale {}", scaleId);
        List<ScaleChatParticipant> participants = scaleChatParticipantRepository.listParticipantsOfScale(scaleId);
        if (!verifyIfScaleChatExists(scaleId)) {
            throw new EventParticipationException("O chat da escala não foi criado");
        }
        boolean isParticipanting = this.scaleChatParticipantRepository.existsScaleChatParticipantByEventScale_IdAndMemberEventScale_MemberMinistry_User_Id(
                scaleId, userId);
        if (isParticipanting) {
            throw new EventParticipationException("Participante já está cadastrado na escala");
        }

        MemberEventScale memberEventScaleUser = this.memberEventScaleRepository.listMemberEventScaleWithUserIdAndScaleId(
                        userId, scaleId)
                .orElseThrow(() -> new EventParticipationException("Usuário na convidado para a escala"));
        EventScale eventScale = this.eventScaleRepository.findById(scaleId)
                .orElseThrow(() -> new EventParticipationException("Escala não existe"));

        return this.scaleChatParticipantRepository.save(new ScaleChatParticipant(eventScale, memberEventScaleUser));
    }

    public UUID getParticipantScaleIdByUserIdAndScaleId(UUID userId, UUID scaleId) {
        return this.scaleChatParticipantRepository.findScaleChatParticipantsByEventScale_IdAndMemberEventScale_MemberMinistry_User_Id(
                        scaleId, userId).orElseThrow(() -> new EventParticipationException("User not found in this scale chat"))
                .getId();
    }
}
