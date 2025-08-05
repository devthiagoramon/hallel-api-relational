package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.exception.ScaleChatParticipantNotFoundException;
import br.hallel.relational.api.app.ministry.model.MessageScaleStatus;
import br.hallel.relational.api.app.ministry.model.ScaleChatMessage;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.repository.MessageScaleStatusRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatMessageRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScaleChatMessageService {

    private final ScaleChatMessageRepository scaleChatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EventScaleRepository scaleRepository;
    private final ScaleChatParticipantRepository scaleChatParticipantRepository;
    private final MessageScaleStatusRepository messageScaleStatusRepository;
    private final FCMSenderService fcmSenderService;

    public ScaleChatMessageResponse sendTextMessage(ScaleChatMessageRequest dto) {

        ScaleChatParticipant senderParticipant = scaleChatParticipantRepository.findById(dto.memberChatSenderId())
                .orElseThrow(() -> new ScaleChatParticipantNotFoundException(
                        "Participant not found by id %s".formatted(dto.memberChatSenderId())));
        EventScale eventScale = scaleRepository.findById(dto.eventScaleId()).orElseThrow(
                () -> new EventScaleNotFoundException("Scale not found by id %s".formatted(dto.eventScaleId())));
        List<ScaleChatParticipant> otherParticipants = scaleChatParticipantRepository.listParticipantsOfScale(
                dto.eventScaleId()).stream().filter(part -> part.getId().equals(dto.memberChatSenderId())).toList();

        ScaleChatMessage messageSaved = scaleChatMessageRepository.save(
                new ScaleChatMessage(eventScale, senderParticipant, dto.content(), dto.contentType()));

        for (ScaleChatParticipant otherParticipant : otherParticipants) {
            messageScaleStatusRepository.save(new MessageScaleStatus(messageSaved, otherParticipant));
            sendNotificationMessageSended(
                    senderParticipant.getMemberEventScale().getMemberMinistry().getUser().getName(),
                    otherParticipant.getMemberEventScale().getMemberMinistry().getUser()
                            .getDevicesUser(), messageSaved, eventScale);
        }

    }

    private void sendNotificationMessageSended(String senderName, List<DeviceNotification> devicesUser,
                                               ScaleChatMessage message, EventScale eventScale) {
        for (DeviceNotification deviceNotification : devicesUser) {
            String fcmToken = deviceNotification.getFcmToken();
            String informativeText = "";
            switch (message.getContentType()) {
                case FILE -> informativeText = senderName.concat("- 📄 Arquivo");
                case TEXT -> informativeText = senderName.concat(
                        "- ".concat(message.getContent().substring(0, 50).concat("...")));
                case IMAGE -> informativeText = senderName.concat("- 📷 Imagem");
            }

            fcmSenderService.sendNotification(
                    fcmToken,
                    "Mensagem da escala",
                    informativeText,
                    getNotificationMessageData(eventScale, message)
            );
        }
    }

    private Map<String, String> getNotificationMessageData(EventScale eventScale, ScaleChatMessage message) {
        Map<String, String> messageData = new HashMap<>();
        messageData.put("scaleId", eventScale.getId().toString());
        messageData.put("messageId", message.getId().toString());
        return messageData;
    }

}
