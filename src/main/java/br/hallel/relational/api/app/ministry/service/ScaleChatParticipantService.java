package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantUserResponse;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatParticipantRepository;
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

    public List<ScaleChatParticipant> createScaleChat(UUID scaleId) {
        log.info("Creating scale chat of scale {}", scaleId);
        EventScale eventScale = eventScaleRepository.findById(scaleId)
                .orElseThrow(() -> new EventScaleNotFoundException("Event scale not found by %s".formatted(scaleId)));
        List<MemberEventScale> membersScale = memberEventScaleRepository.findAllByEventScale_Id(scaleId);
        List<ScaleChatParticipant> response = new ArrayList<>();
        for (MemberEventScale memberEventScale : membersScale) {
            ScaleChatParticipant memberChatSaved = scaleChatParticipantRepository.save(
                    new ScaleChatParticipant(eventScale, memberEventScale));
            response.add(memberChatSaved);
        }
        return response;
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
        List<ScaleChatParticipant> participants = scaleChatParticipantRepository.findScaleChatParticipantsByEventScale_Id(scaleId);
        return !participants.isEmpty();
    }
}
