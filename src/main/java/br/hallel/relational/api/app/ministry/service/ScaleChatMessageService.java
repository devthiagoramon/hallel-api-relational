package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.exception.EventParticipationException;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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
        List<ScaleChatParticipantResponse> otherParticipants = scaleChatParticipantRepository.listParticipantsOfScaleWhoNotSender(
                dto.eventScaleId(), dto.memberChatSenderId());

        ScaleChatMessage messageSaved = scaleChatMessageRepository.save(
                new ScaleChatMessage(eventScale, senderParticipant, dto.content(), dto.contentType()));

        ScaleChatMessageResponse response = getScaleChatMessageResponse(eventScale, messageSaved);

        String destinationSocket = "/topic/scale/chat/" + eventScale.getId().toString();

        simpMessagingTemplate.convertAndSend(destinationSocket, response);
        sendNotificationsToParticipants(senderParticipant.getMemberEventScale().getMemberMinistry().getUser().getName(),
                otherParticipants, messageSaved, eventScale);
        return response;
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
        List<ScaleChatParticipantResponse> otherParticipants = scaleChatParticipantRepository.listParticipantsOfScaleWhoNotSender(
                dto.eventScaleId(), dto.memberChatSenderId());

        ScaleChatMessage messageSaved = scaleChatMessageRepository.save(
                new ScaleChatMessage(eventScale, senderParticipant, dto.contentType()));

        String fileName = GoogleBucketUtils.getImageName(messageSaved.getId().toString(),
                ScaleChatMessage.class.getSimpleName());
        String fileUrl = googleBucketService.sendFileToBucket(file, fileName);
        messageSaved.setContent(fileUrl);

        ScaleChatMessage messageUpdateWithContent = scaleChatMessageRepository.save(messageSaved);
        ScaleChatMessageResponse response = getScaleChatMessageResponse(eventScale, messageUpdateWithContent);
        String destinationSocket = "/topic/scale/chat/" + eventScale.getId().toString();

        simpMessagingTemplate.convertAndSend(destinationSocket, response);
        sendNotificationsToParticipants(senderParticipant.getMemberEventScale().getMemberMinistry().getUser().getName(),
                otherParticipants, messageSaved, eventScale);
        return response;
    }

    @Async
    public void sendNotificationsToParticipants(
            String senderName,
            List<ScaleChatParticipantResponse> participants,
            ScaleChatMessage message,
            EventScale eventScale) {

        for (ScaleChatParticipantResponse participant : participants) {
            try {
                // Coloque sua lógica de envio aqui
                sendNotificationMessageSended(
                        senderName,
                        participant.userParticipant().getDevicesUser(),
                        message,
                        eventScale
                );
            } catch (Exception e) {
                // É CRUCIAL tratar exceções aqui para que um erro
                // não pare o envio para os outros participantes.
                log.error("Failed to send notification to participant {}", participant.userParticipant().getId(), e);
            }
        }
    }

    private ScaleChatMessageResponse getScaleChatMessageResponse(
            EventScale eventScale,
            ScaleChatMessage message) {

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
        ScaleChatMessageResponse response = new ScaleChatMessageResponse(
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
        simpMessagingTemplate.convertAndSend(destination, response);
        return response;
    }

    public Page<ScaleChatMessageResponse> listMessagesOfScaleChatForUser(UUID scaleId, UUID userId, Pageable pageable) {
        ScaleChatParticipant scaleChatParticipant = getScaleChatParticipantByUserId(scaleId, userId);

        return this.scaleChatMessageRepository.listMessagesWithStatus(
                scaleId, scaleChatParticipant.getId(), pageable);
    }

    private ScaleChatParticipant getScaleChatParticipantByUserId(UUID scaleId, UUID userId) {
        return this.scaleChatParticipantRepository.findScaleChatParticipantsByEventScale_IdAndMemberEventScale_MemberMinistry_User_Id(
                        scaleId, userId)
                .orElseThrow(() -> new EventParticipationException("Usuário não encontrado ou não participa do chat"));
    }

    public List<StatusReadingMessageUserResponse> listStatusDeliveryPerUser(UUID messageId) {
        return this.scaleChatMessageRepository.listStatusDeliverPerUser(messageId);
    }


    public MessageScaleDeliveryStatus readMessageForUser(UUID userId, UUID messageId) {
        MessageScaleStatus messageScaleStatus = this.messageScaleStatusRepository.findByMessage_IdAndChatParticipant_MemberEventScale_MemberMinistry_User_Id(
                messageId, userId).orElseThrow(
                (() -> new EventParticipationException("Usuário não encontrado ou não participa do chat")));

        String destinationSocket = "/topic/scale/chat/" + messageScaleStatus.getMessage().getScale().getId()
                .toString() + "/message/status";

        simpMessagingTemplate.convertAndSend(destinationSocket, new MessageReadSocketResponse(
                messageId,
                MessageScaleDeliveryStatus.READ
        ));
        messageScaleStatus.setStatus(MessageScaleDeliveryStatus.READ);
        this.messageScaleStatusRepository.save(messageScaleStatus);
        return messageScaleStatus.getStatus();
    }

    public MessageScaleDeliveryStatus userReceivedMessage(UUID messageId, UUID userId) {
        MessageScaleStatus messageScaleStatus = this.messageScaleStatusRepository.findByMessage_IdAndChatParticipant_MemberEventScale_MemberMinistry_User_Id(
                messageId, userId).orElseThrow(
                (() -> new EventParticipationException("Usuário não encontrado ou não participa do chat")));
        String destinationSocket = "/topic/scale/chat/" + messageScaleStatus.getMessage().getScale().getId()
                .toString() + "/message/status";

        simpMessagingTemplate.convertAndSend(destinationSocket, new MessageReadSocketResponse(
                messageId,
                MessageScaleDeliveryStatus.RECEIVED
        ));
        messageScaleStatus.setStatus(MessageScaleDeliveryStatus.RECEIVED);
        this.messageScaleStatusRepository.save(messageScaleStatus);
        return messageScaleStatus.getStatus();
    }
}
