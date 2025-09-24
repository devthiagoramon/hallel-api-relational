package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.global.exception.FileNullException;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.ministry.dto.*;
import br.hallel.relational.api.app.ministry.exception.ScaleChatMessageNotFound;
import br.hallel.relational.api.app.ministry.exception.ScaleChatParticipantNotFoundException;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.MessageScaleStatusRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatMessageRepository;
import br.hallel.relational.api.app.ministry.repository.ScaleChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
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
                () -> new EventScaleNotFoundException("event.scale.not.found", dto.eventScaleId().toString()));
        List<ScaleChatParticipant> otherParticipants = scaleChatParticipantRepository.listParticipantsOfScale(
                dto.eventScaleId()).stream().filter(part -> !part.getId().equals(dto.memberChatSenderId())).toList();

        ScaleChatMessage messageSaved = scaleChatMessageRepository.save(
                new ScaleChatMessage(eventScale, senderParticipant, dto.content(), dto.contentType()));


        String destinationSocket = "/topic/scale/chat/" + eventScale.getId().toString();

        simpMessagingTemplate.convertAndSend(destinationSocket, messageSaved);
        return getScaleChatMessageResponseAndSendNotification(senderParticipant, eventScale, otherParticipants,
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
                () -> new EventScaleNotFoundException("event.scale.not.found", dto.eventScaleId().toString()));
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
                messageUpdateWithContent);
    }

    private ScaleChatMessageResponse getScaleChatMessageResponseAndSendNotification(
            ScaleChatParticipant senderParticipant,
            EventScale eventScale,
            List<ScaleChatParticipant> otherParticipants,
            ScaleChatMessage message) {
        for (ScaleChatParticipant otherParticipant : otherParticipants) {
            messageScaleStatusRepository.save(
                    new MessageScaleStatus(message, otherParticipant));
            sendNotificationMessageSended(
                    senderParticipant.getMemberEventScale().getMemberMinistry().getUser().getName(),
                    otherParticipant.getMemberEventScale().getMemberMinistry().getUser()
                            .getDevicesUser(), message, eventScale);
        }

        return new ScaleChatMessageResponse(
                message.getId(),
                eventScale.getId(),
                message.getMemberChatSender().getId(),
                message.getMemberChatSender().getMemberEventScale().getMemberMinistry().getUser(),
                message.getContent(),
                message.getContentType(),
                message.getSentAt(),
                message.getUpdatedAt(),
                MessageScaleDeliveryStatus.SENT,
                message.getVisibility()
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

    public ScaleChatMessageResponse deleteMessage(UUID messageId) {
        log.info("Delete message with id {}", messageId);
        ScaleChatMessage scaleChatMessage = scaleChatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ScaleChatMessageNotFound("Message not found by id %s".formatted(messageId)));

        if (scaleChatMessage.getContentType() != ScaleMessageType.TEXT) {
            String fileName = GoogleBucketUtils.getImageName(scaleChatMessage.getId().toString(),
                    ScaleChatMessage.class.getSimpleName());
            googleBucketService.deleteFileOfBucket(fileName);
        }

        scaleChatMessage.setVisibility(ScaleChatMessageVisibility.DELETED);
        scaleChatMessage.setContentType(ScaleMessageType.TEXT);
        scaleChatMessage.setContent("Mensagem apagada");
        ScaleChatMessage deletedMessage = scaleChatMessageRepository.save(scaleChatMessage);

        String destination = "/topic/scale/chat/" + deletedMessage.getScale().getId().toString();

        ScaleMessageUpdateEvent updateEvent = new ScaleMessageUpdateEvent(
                deletedMessage.getId(),
                ScaleMessageUpdateEventTypes.DELETE,
                null
        );

        simpMessagingTemplate.convertAndSend(destination, updateEvent);

        return new ScaleChatMessageResponse(
                scaleChatMessage.getId(),
                scaleChatMessage.getScale().getId(),
                scaleChatMessage.getMemberChatSender().getId(),
                scaleChatMessage.getMemberChatSender().getMemberEventScale().getMemberMinistry().getUser(),
                scaleChatMessage.getContent(),
                scaleChatMessage.getContentType(),
                scaleChatMessage.getSentAt(),
                scaleChatMessage.getUpdatedAt(),
                MessageScaleDeliveryStatus.SENT,
                scaleChatMessage.getVisibility()
        );
    }

    public ScaleChatMessageResponse editMessage(ScaleChatMessageRequestEdit request) {
        log.info("Edit message with id {}", request.messageId());
        ScaleChatMessage scaleChatMessage = scaleChatMessageRepository.findById(request.messageId())
                .orElseThrow(() -> new ScaleChatMessageNotFound(
                        "Message not found by id %s".formatted(request.messageId())));

        scaleChatMessage.setContent(request.content());
        scaleChatMessage.setUpdatedAt(OffsetDateTime.now());
        ScaleChatMessage messageUpdated = scaleChatMessageRepository.save(scaleChatMessage);
        String destination = "/topic/scale/chat/" + messageUpdated.getScale().getId().toString();
        ScaleMessageUpdateEvent updateEvent = new ScaleMessageUpdateEvent(
                messageUpdated.getId(),
                ScaleMessageUpdateEventTypes.UPDATE,
                messageUpdated
        );
        simpMessagingTemplate.convertAndSend(destination, updateEvent);
        return new ScaleChatMessageResponse(
                scaleChatMessage.getId(),
                scaleChatMessage.getScale().getId(),
                scaleChatMessage.getMemberChatSender().getId(),
                scaleChatMessage.getMemberChatSender().getMemberEventScale().getMemberMinistry().getUser(),
                scaleChatMessage.getContent(),
                scaleChatMessage.getContentType(),
                scaleChatMessage.getSentAt(),
                scaleChatMessage.getUpdatedAt(),
                MessageScaleDeliveryStatus.SENT,
                scaleChatMessage.getVisibility()
        );
    }

    public Page<ScaleChatMessageResponse> listMessagesOfScaleChatForUser(UUID scaleId, UUID userId, Pageable pageable) {
        return this.scaleChatMessageRepository.listMessagesWithStatus(
                scaleId, userId, pageable);
    }

    public List<StatusReadingMessageUserResponse> listStatusDeliveryPerUser(UUID messageId) {
        return this.scaleChatMessageRepository.listStatusDeliverPerUser(messageId);
    }
}
