package br.hallel.relational.api.app.integrationtests.ministry.service;

import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.service.ScaleChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScaleMessageChatServiceTest {

    @Autowired
    private ScaleChatMessageService scaleChatMessageService;

    public ScaleChatMessageResponse createChatService(ScaleChatMessageRequest request) {
        return this.scaleChatMessageService.sendTextMessage(request);
    }
}
