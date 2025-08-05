package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.global.exception.FileNullException;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.exception.ScaleChatParticipantNotFoundException;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.MessageScaleStatusRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatMessageRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j @Service
@RequiredArgsConstructor
public class ScaleChatMessageService {

    private final ScaleChatMessageRepository scaleChatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EventScaleRepository scaleRepository;
    private final ScaleChatParticipantRepository scaleChatParticipantRepository;
    private final MessageScaleStatusRepository messageScaleStatusRepository;
    private final FCMSenderService fcmSenderService;
    private final GoogleBucketService googleBucketService;

    public ScaleChatMessageResponse sendTextMessage(ScaleChatMessageRequest dto) {
        log.info("Sending text message for scale {} ", dto.eventScaleId());
        ScaleChatParticipant senderParticipant = scaleChatParticipantRepository.listByIdWithUserInfo(
                        dto.memberChatSenderId())
                .orElseThrow(() -> new ScaleChatParticipantNotFoundException(
                        "Participant not found by id %s".formatted(dto.memberChatSenderId())));
        EventScale eventScale = scaleRepository.findById(dto.eventScaleId()).orElseThrow(
                () -> new EventScaleNotFoundException("Scale not found by id %s".formatted(dto.eventScaleId())));
        List<ScaleChatParticipant> otherParticipants = scaleChatParticipantRepository.listParticipantsOfScale(
                dto.eventScaleId()).stream().filter(part -> !part.getId().equals(dto.memberChatSenderId())).toList();

        ScaleChatMessage messageSaved = scaleChatMessageRepository.save(
                new ScaleChatMessage(eventScale, senderParticipant, dto.content(), dto.contentType()));


        String destinationSocket = "/topic/scale/chat/" + eventScale.getId().toString();

        simpMessagingTemplate.convertAndSend(destinationSocket, messageSaved);
        return getScaleChatMessageResponseAndSendNotification(senderParticipant, eventScale, otherParticipants,
                messageSaved,
                messageSaved);
    }

    public ScaleChatMessageResponse sendFileMessage(MultipartFile file, ScaleChatMessageRequest dto) {
        log.info("Sending file message for scale {} ", dto.eventScaleId());

        if (file == null || file.isEmpty()) {
            throw new FileNullException("File is null or empty!");
        }

        ScaleChatParticipant senderParticipant = scaleChatParticipantRepository.listByIdWithUserInfo(
                        dto.memberChatSenderId())
                .orElseThrow(() -> new ScaleChatParticipantNotFoundException(
                        "Participant not found by id %s".formatted(dto.memberChatSenderId())));
        EventScale eventScale = scaleRepository.findById(dto.eventScaleId()).orElseThrow(
                () -> new EventScaleNotFoundException("Scale not found by id %s".formatted(dto.eventScaleId())));
        List<ScaleChatParticipant> otherParticipants = scaleChatParticipantRepository.listParticipantsOfScale(
                dto.eventScaleId()).stream().filter(part -> !part.getId().equals(dto.memberChatSenderId())).toList();

        ScaleChatMessage messageSaved = scaleChatMessageRepository.save(
                new ScaleChatMessage(eventScale, senderParticipant, dto.contentType()));

        String fileName = GoogleBucketUtils.getImageName(messageSaved.getId().toString(),
                ScaleChatMessage.class.getSimpleName());
        String fileUrl = googleBucketService.sendFileToBucket(file, fileName);
        messageSaved.setContent(fileUrl);

        ScaleChatMessage messageUpdateWithContent = scaleChatMessageRepository.save(messageSaved);

        String destinationSocket = "/topic/scale/chat/" + eventScale.getId().toString();

        simpMessagingTemplate.convertAndSend(destinationSocket, messageUpdateWithContent);

        return getScaleChatMessageResponseAndSendNotification(senderParticipant, eventScale, otherParticipants,
                messageSaved,
                messageUpdateWithContent);
    }

    private ScaleChatMessageResponse getScaleChatMessageResponseAndSendNotification(
            ScaleChatParticipant senderParticipant,
            EventScale eventScale,
            List<ScaleChatParticipant> otherParticipants,
            ScaleChatMessage messageSaved,
            ScaleChatMessage messageUpdateWithContent) {
        for (ScaleChatParticipant otherParticipant : otherParticipants) {
            messageScaleStatusRepository.save(
                    new MessageScaleStatus(messageSaved, otherParticipant));
            sendNotificationMessageSended(
                    senderParticipant.getMemberEventScale().getMemberMinistry().getUser().getName(),
                    otherParticipant.getMemberEventScale().getMemberMinistry().getUser()
                            .getDevicesUser(), messageUpdateWithContent, eventScale);
        }

        return new ScaleChatMessageResponse(
                messageSaved.getId(),
                eventScale.getId(),
                messageSaved.getMemberChatSender().getId(),
                messageSaved.getMemberChatSender().getMemberEventScale().getMemberMinistry().getUser(),
                messageSaved.getContent(),
                messageSaved.getContentType(),
                messageSaved.getSentAt(),
                messageSaved.getUpdatedAt(),
                MessageScaleDeliveryStatus.SENT
        );
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
