package br.hallel.relational.api.app.integrationtests.ministry.service;

import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.service.ScaleChatParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScaleChatServiceTest {

    @Autowired
    private ScaleChatParticipantService scaleChatParticipantService;

    public List<ScaleChatParticipant> createScaleTest(UUID scaleId) {
        return this.scaleChatParticipantService.createScaleChat(scaleId);
    }

    public ScaleChatParticipant addParticipantInScale(UUID userId, UUID participantId) {
        return this.scaleChatParticipantService.addParticipantFromScaleChatParticipant(userId, participantId);
    }

    public boolean removeParticipantInScale(UUID scaleChatParticipantId) {
        this.scaleChatParticipantService.removeParticipantFromScaleChat(scaleChatParticipantId);
        return true;
    }

}
