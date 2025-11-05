package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.email.dto.EmailParticipationDTO;
import br.hallel.relational.api.app.email.service.EmailEventParticipationService;
import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.*;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.model.enum_type.AgeGroup;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.TransactionType;
import br.hallel.relational.api.app.event.model.enum_type.UserFunctionInEvent;
import br.hallel.relational.api.app.event.repository.*;
import br.hallel.relational.api.app.event.utils.EventParticipationUtils;
import br.hallel.relational.api.app.global.pdf.PdfGenerationService;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.PixPaymentData;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.GenerateReceiptException;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.MercadoPagoAPIException;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.MercadoPagoException;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.utils.UserUtils;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserEventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventTransactionRepository eventTransactionRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final SimpMessagingTemplate template;
    private final PdfGenerationService pdfGenerationService;
    private final LimitEventAgeGroupRepository limitEventAgeGroupRepository;
    private final EventInviteRepository eventInviteRepository;
    private final EmailEventParticipationService emailEventParticipationService;
    private final EventParticipationUtils eventParticipationUtils;
    private final EventQueueParticipantRepository eventQueueParticipantRepository;

    public EventParticipationResponse joinTheEvent(UUID generatedPaymentId, UUID userId, EventParticipateDTO dto) {

        Event event = this.eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.getEventId().toString())
        );

        long currentParticipants = eventParticipationRepository.countByEvent_IdAndStatusPaymentEventParticipationNot(
                event.getId(), StatusPaymentEventParticipation.NAO_PAGO
        );

        List<EventInviteBatch> batches = event.getEventInviteBatches().stream()
                .sorted(Comparator.comparingInt(EventInviteBatch::getMaxNumber))
                .toList();

        EventInviteBatch currentBatch = null;
        if (!event.getItsFree()) { // Só valida lote se o evento não for gratuito
            if (batches.isEmpty()) {
                log.warn("Evento pago {} não possui lotes (EventInviteBatch) cadastrados.", event.getId());
                throw new EventIllegalArumentException("Nenhum lote de inscrição configurado para este evento.");
            }

            for (EventInviteBatch batch : batches) {
                if (currentParticipants < batch.getMaxNumber()) {
                    currentBatch = batch; // Encontrou o lote atual!
                    break;
                }
            }

            if (currentBatch == null) {
                log.warn("Inscrição rejeitada para o evento {}. Número de participantes ({}) excede o último lote.",
                        event.getId(), currentParticipants);
                throw new EventBatchSoldOutException("Todos os lotes para este evento estão esgotados.");
            }
            log.info("Inscrição no evento {} validada. Lote ativo encontrado. Acréscimo: R$ {}",
                    event.getId(), currentBatch.getValueIncrease());
        }

        Optional<EventInvite> eventInviteOptional = Optional.empty();
        if (dto.getEventInviteId() != null) {
            eventInviteOptional = this.eventInviteRepository.findById(dto.getEventInviteId());
        }

        Optional<User> optionalUser = (userId != null)
                ? this.userRepository.findById(userId)
                : Optional.empty();
        if (optionalUser.isPresent()) {

            boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(optionalUser.get(), event);
            if (alreadyParticipating) {
                log.warn("Usuário ID {} já está participando do evento ID {}. Lançando exceção.", userId,
                        dto.getEventId());
                throw new EventIllegalArumentException("User already participating in this event.");
            }
        }


        EventParticipation eventParticipation = new EventParticipation();
        optionalUser.ifPresent(eventParticipation::setUser);
        eventParticipation.setEvent(event);
        eventParticipation.setEmail(dto.getEmail());
        eventParticipation.setPhoneNumber(dto.getPhoneNumber());
        eventParticipation.setName(dto.getName());
        eventParticipation.setUserFunctionInEvent(UserFunctionInEvent.PARTICIPANTE);
        eventParticipation.setHasParticipated(false);
        eventParticipation.setCommunity(dto.getCommunity().trim());

        if (eventParticipation.getCommunity().toLowerCase().contains("hallel")) {
            eventParticipation.setEventParticipationType(EventParticipationType.COMUNIDADE);
            eventParticipation.setFormation(dto.getFormation().trim());
        } else {
            eventParticipation.setEventParticipationType(EventParticipationType.OUTRO);
        }

        eventParticipation.setIsMarried(dto.getIsMarried());
        eventParticipation.setDateBirth(dto.getDateBirth());

        String qrCodeBase64 = null;
        boolean isAnonymous = optionalUser.isEmpty();

        boolean isPaidEvent = eventInviteOptional.isPresent();

        User user = !isAnonymous ? optionalUser.get() : null;

        Date birthDate = !isAnonymous
                ? user.getDateBirth()
                : Date.from(dto.getDateBirth().toInstant());
        log.info("Data de nascimento do usuário: {}", birthDate);
        LocalDate birthLocalDate;

        if (birthDate instanceof java.sql.Date sqlDate) {
            birthLocalDate = sqlDate.toLocalDate();
        } else {
            birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        int years = eventParticipationUtils.calculateAge(birthLocalDate);
        ValidateAgeParticipantResponse validation =
                eventParticipationUtils.validateAgeParticipant(years, event);

        if (validation.limiteReached() != null && validation.limiteReached() == AgeGroup.EXCEDIDO) {
            log.info("Participação Salva na fila.");
            EventParticipation participationSaved = this.eventParticipationRepository.save(eventParticipation);

            EventQueueParticipant queueParticipant = new EventQueueParticipant(participationSaved, event);

            EventQueueParticipant savedQueueParticipant = this.eventQueueParticipantRepository.save(queueParticipant);

            int participantQueuePosition = this.eventParticipationUtils.getParticipantQueuePosition(savedQueueParticipant);
            this.emailEventParticipationService.sendNotificationEventQueue(
                    new EmailParticipationDTO(
                            eventParticipation.getEmail(),
                            eventParticipation.getName(),
                            event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            event.getTitle()
                    ),
                    participantQueuePosition,
                    event.getId().toString()
            );
            return EventParticipationResponse.toEventParticipationLimitReached(true,
                    validation.ageGroup(),
                    event.getId(),
                    !isAnonymous ? user.getId() : null);
        }

        if (isPaidEvent) {
            EventInvite eventInvite = eventInviteOptional.get();
            eventParticipation.setEventInviteAssociated(eventInvite);
            double baseValue = eventInvite.getValue();
            double increaseValue = (currentBatch != null && currentBatch.getValueIncrease() != null)
                    ? currentBatch.getValueIncrease()
                    : 0.0;
            double finalPrice = baseValue + increaseValue;
            try {
                String fullName = !isAnonymous ? user.getName() : dto.getName();
                String[] nameParts = UserUtils.splitFullName(fullName);
                String firstName = nameParts[0];
                String lastName = nameParts[1];
                String cpfParaPagamento = null;

                if (!isAnonymous && user.getCpf() != null) {
                    cpfParaPagamento = user.getCpf();
                } else if (dto.getCpf() != null) {
                    cpfParaPagamento = dto.getCpf();
                }

                if (cpfParaPagamento == null || cpfParaPagamento.isEmpty()) {
                    throw new UserValidationException("User CPF is required to make the payment.");
                }


                CreatePixPaymentRequestDTO paymentRequestDTO =
                        new CreatePixPaymentRequestDTO(
                                BigDecimal.valueOf(finalPrice),
                                event.getTitle(),
                                !isAnonymous ? user.getEmail() : dto.getEmail(),
                                firstName,
                                lastName,
                                cpfParaPagamento
                        );

                Payment payment = mercadoPagoClient.createPixPayment(paymentRequestDTO, generatedPaymentId);

                if (payment != null && payment.getPointOfInteraction() != null &&
                        payment.getPointOfInteraction().getTransactionData() != null) {
                    eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
                    eventParticipation.setPixTxid(payment.getPointOfInteraction().getTransactionData().getQrCode());
                    eventParticipation.setMercadoPagoPaymentId(payment.getId());
                    qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
                    String linkCodePayment = payment.getPointOfInteraction().getTransactionData().getQrCode();

                    template.convertAndSend("/topic/payments/" + generatedPaymentId,
                            new PaymentStatusDTO(qrCodeBase64, linkCodePayment,
                                    StatusPaymentEventParticipation.PENDENTE));


                    log.info("Pagamento Pix criado com sucesso de id {}. TXID: {}", generatedPaymentId,
                            eventParticipation.getPixTxid());
                    PixPaymentData pixData = new PixPaymentData(
                            payment.getId(),
                            BigDecimal.valueOf(finalPrice),
                            event.getTitle(),
                            linkCodePayment,
                            qrCodeBase64,
                            payment.getDateOfExpiration().toLocalDateTime()
                    );

                    // 5. CHAMADA DO NOVO SERVIÇO DE E-MAIL
                    // Note que a assinatura do método de e-mail mudou para aceitar o PixPaymentData
                    EmailParticipationDTO emailDto = new EmailParticipationDTO(
                            eventParticipation.getEmail(),
                            eventParticipation.getName(),
                            event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            event.getTitle()
                    );
                    emailEventParticipationService.sendPaymentJoinEvent(
                            emailDto,
                            event.getStartTime(),
                            event.getEndTime(),
                            event.getId().toString(),
                            pixData
                    );
                } else {
                    log.error("Resposta do Mercado Pago incompleta, dados de transação ou de interação nulos.");
                    throw new RuntimeException("Erro ao processar a resposta do Mercado Pago.");
                }

            } catch (MPApiException apiException) {
                log.error("Erro na API do Mercado Pago. Status: {}, Mensagem: {}. Detalhes: {}",
                        apiException.getStatusCode(),
                        apiException.getMessage(),
                        apiException.getApiResponse().getContent());
                throw new RuntimeException("Erro ao criar pagamento Pix. Por favor, tente novamente.", apiException);
            } catch (MPException | RuntimeException e) {
                log.error("Erro ao criar pagamento Pix no Mercado Pago: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao criar pagamento Pix. Por favor, tente novamente.", e);
            }
        } else {
            try {

                EmailParticipationDTO emailDto = new EmailParticipationDTO(
                        eventParticipation.getEmail(),
                        eventParticipation.getName(),
                        event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        event.getTitle()
                );

                emailEventParticipationService.sendComprovantEventParticipation(
                        emailDto, event.getId().toString(),
                        event.getWhatsAppGroupLink()
                );
            } catch (Exception e) {
                log.error("Erro ao enviar e-mail de comprovante de participação: {}", e.getMessage(), e);
                throw new GenerateReceiptException("Erro ao enviar e-mail de comprovante de participação. " + e);
            }

            eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);
            eventParticipation.setPaidDate(OffsetDateTime.now(ZoneId.of("UTC")));
        }

        EventParticipation participationSaved = eventParticipationRepository.save(eventParticipation);
        log.info("Participação do evento salva no banco de dados com ID: {}", participationSaved.getId());
        return EventParticipationResponse.toEventParticipation(participationSaved, qrCodeBase64);

    }


    public EventPayParticipationDetails payAnEvent(UUID userId, UUID eventId) {
        log.info("Paying an event");
        Event event = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );

        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (!alreadyParticipating) {
            log.warn("Usuário ID {} não está participando do evento ID {}. Lançando exceção.", userId,
                    eventId);
            throw new EventParticipationException("participation.event.not.found");
        }

        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId).get();

        if (participation.getPixTxid() == null) {
            throw new EventParticipationException("This user no have an pix to pay");
        }

        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO) {
            throw new EventParticipationException("This user already pay the event.");
        }

        byte[] pixTxidBytes = participation.getPixTxid().getBytes();
        try {
            String qrCode = mercadoPagoClient.getPaymentQRCode(participation.getMercadoPagoPaymentId());

            template.convertAndSend("/topic/payments/" + user.getId(),
                    new PaymentStatusDTO(qrCode, participation.getPixTxid(),
                            StatusPaymentEventParticipation.PENDENTE));
        } catch (Exception e) {
            log.error(e.getMessage());
        }


        return new EventPayParticipationDetails(Base64.getEncoder().encodeToString(pixTxidBytes),
                participation.getPixTxid(),
                participation.getEventInviteAssociated().getValue());
    }


    public boolean leaveTheEventAsUser(UUID eventId, UUID userId) {
        Event event = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );
        this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        log.debug("Leave the event");
        log.debug("User ID: {}", userId);
        log.debug("Event ID: {}", eventId);
        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"
                ));

        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO) {
            log.info(
                    "Participação do evento está paga. Tentando solicitar o reembolso para o pagamento de Mercado Pago ID: {}",
                    participation.getMercadoPagoPaymentId());
            try {

                this.mercadoPagoClient.requestRefund(
                        participation.getMercadoPagoPaymentId(),
                        participation.getAmountPaid()
                );
            } catch (PaymentRefundException e) {

                log.error("Falha ao solicitar o reembolso para o pagamento {} do evento {}. Motivo: {}",
                        participation.getMercadoPagoPaymentId(), eventId, e.getMessage());

            }
        }

        if (participation.getMercadoPagoPaymentId() != null) {
            eventTransactionRepository.findByMercadoPagoPaymentId(participation.getMercadoPagoPaymentId())
                    .ifPresent(eventTransactionRepository::delete);
            log.info("Transação do evento para o pagamento {} deletada com sucesso.",
                    participation.getMercadoPagoPaymentId());
        }

        AgeGroup ageGroup = EventParticipationUtils.getAgeGroup(eventParticipationUtils.calculateAge(
                participation.getDateBirth().toLocalDate()
        ));

        LimitEventAgeGroup limit = limitEventAgeGroupRepository
                .findByEventIdAndAgeGroup(event.getId(), ageGroup)
                .orElseThrow(() -> new RuntimeException(
                        "Limite de vagas não configurado para faixa etária: " + ageGroup));

        //Condição verifica se o limite foi atingido
        if (limit.getLimitQuantity() == limit.getCurrentQuantity()) {
            log.info("Ocupação máxima para a faixa etária {} já foi atingida. Nenhuma ação necessária no limite.",
                    ageGroup);

            //bloco para verificar se o limite foi atingido e se tem alguém na fila
            this.eventQueueParticipantRepository.findFirstByEvent_IdOrderByQueuedAtAsc(event.getId())
                    .ifPresent(queueParticipant -> {
                        EventParticipation participantToNotify = queueParticipant.getEventParticipation();
                        this.emailEventParticipationService.sendQueueSpaceAvailableNotification(
                                new EmailParticipationDTO(
                                        participantToNotify.getEmail(),
                                        participantToNotify.getName(),
                                        event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                        event.getTitle()
                                ),
                                event.getId().toString()
                        );
                        this.eventQueueParticipantRepository.delete(queueParticipant);
                        log.info("Participante {} notificado sobre a vaga disponível no evento {}.",
                                participantToNotify.getEmail(), event.getTitle());
                    });

        } else if (limit.getCurrentQuantity() <= 0) {
            log.warn("A quantidade atual para a faixa etária {} está em zero ou negativa. Verifique os dados.",
                    ageGroup);

        }
        limit.setCurrentQuantity(limit.getCurrentQuantity() - 1);
        limitEventAgeGroupRepository.save(limit);

        eventParticipationRepository.delete(participation);
        log.info("Participação do usuário {} no evento {} deletada com sucesso.", userId, eventId);

        //email service
        EmailParticipationDTO emailDTO = new EmailParticipationDTO(
                participation.getEmail(),
                participation.getName(),
                event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                event.getTitle()
        );
        emailEventParticipationService.sendRefundEventParticipation(
                emailDTO,
                participation.getAmountPaid() == null ? 0 : participation.getAmountPaid()
        );
        return true;

    }

    public boolean leaveTheEventByAdmin(UUID eventId, String userEmail) {
        Event event = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );

        log.debug("Leave the event");
        log.debug("User ID: {}", userEmail);
        log.debug("Event ID: {}", eventId);
        EventParticipation participation = eventParticipationRepository.findByEmailAndEvent_Id(userEmail, eventId)
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"
                ));

        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO) {
            log.info(
                    "Participação do evento está paga. Tentando solicitar o reembolso para o pagamento de Mercado Pago ID: {}",
                    participation.getMercadoPagoPaymentId());
            try {

                this.mercadoPagoClient.requestRefund(
                        participation.getMercadoPagoPaymentId(),
                        participation.getAmountPaid()
                );
            } catch (PaymentRefundException e) {

                log.error("Falha ao solicitar o reembolso para o pagamento {} do evento {}. Motivo: {}",
                        participation.getMercadoPagoPaymentId(), eventId, e.getMessage());

            }
        }

        if (participation.getMercadoPagoPaymentId() != null) {
            eventTransactionRepository.findByMercadoPagoPaymentId(participation.getMercadoPagoPaymentId())
                    .ifPresent(eventTransactionRepository::delete);
            log.info("Transação do evento para o pagamento {} deletada com sucesso.",
                    participation.getMercadoPagoPaymentId());
        }

        AgeGroup ageGroup = EventParticipationUtils.getAgeGroup(eventParticipationUtils.calculateAge(
                participation.getDateBirth().toLocalDate()
        ));

        LimitEventAgeGroup limit = limitEventAgeGroupRepository
                .findByEventIdAndAgeGroup(event.getId(), ageGroup)
                .orElseThrow(() -> new RuntimeException(
                        "Limite de vagas não configurado para faixa etária: " + ageGroup));


        if (limit.getLimitQuantity() == limit.getCurrentQuantity()) {
            log.info("Ocupação máxima para a faixa etária {} já foi atingida. Nenhuma ação necessária no limite.",
                    ageGroup);
            this.eventQueueParticipantRepository.findFirstByEvent_IdOrderByQueuedAtAsc(event.getId())
                    .ifPresent(queueParticipant -> {
                        EventParticipation participantToNotify = queueParticipant.getEventParticipation();
                        this.emailEventParticipationService.sendQueueSpaceAvailableNotification(
                                new EmailParticipationDTO(
                                        participantToNotify.getEmail(),
                                        participantToNotify.getName(),
                                        event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                        event.getTitle()
                                ),
                                event.getId().toString()
                        );
                        this.eventQueueParticipantRepository.delete(queueParticipant);
                        log.info("Participante {} notificado sobre a vaga disponível no evento {}.",
                                participantToNotify.getEmail(), event.getTitle());
                    });

        } else if (limit.getCurrentQuantity() <= 0) {
            log.warn("A quantidade atual para a faixa etária {} está em zero ou negativa. Verifique os dados.",
                    ageGroup);

        }
        limit.setCurrentQuantity(limit.getCurrentQuantity() - 1);
        limitEventAgeGroupRepository.save(limit);

        eventParticipationRepository.delete(participation);
        log.info("Participação do usuário {} no evento {} deletada com sucesso.", userEmail, eventId);
        EmailParticipationDTO dto = new EmailParticipationDTO(
                participation.getEmail(),
                participation.getName(),
                event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                event.getTitle()
        );
        emailEventParticipationService.sendRefundEventParticipation(
                dto,
                participation.getAmountPaid()
        );
        return true;
    }

    public EventParticipationResponse editParticipationEvent(UUID userId, EventParticipationDTO dto) {

        eventRepository.findById(dto.eventID()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.eventID().toString())
        );
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, dto.eventID())
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"
                ));

        if (dto.statusPaymentEventParticipation() != null) {
            participation.setStatusPaymentEventParticipation(dto.statusPaymentEventParticipation());
        }

        if (dto.hasParticipated() != null) {
            participation.setHasParticipated(dto.hasParticipated());
        }

        if (dto.userFunctionInEvent() != null) {
            participation.setUserFunctionInEvent(dto.userFunctionInEvent());
        }

        if (dto.community() != null) {
            participation.setCommunity(dto.community());
        }

        if (dto.amountPaid() != null) {
            participation.setAmountPaid(dto.amountPaid());

            if (dto.amountPaid() >= participation.getEventInviteAssociated().getValue()) {
                participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);

                EventTransaction transaction = new EventTransaction();
                transaction.setEvent(participation.getEvent());
                transaction.setValue(dto.amountPaid());
                transaction.setDateTransaction(new Date());
                transaction.setTransactionType(TransactionType.ENTRADA);
                transaction.setDescription("Pagamento atualizado do Participante " +
                        participation.getUser().getName() +
                        " para o Evento " + participation.getEvent().getTitle());
                eventTransactionRepository.save(transaction);
            } else {
                participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
            }
        }

        EventParticipation updated = eventParticipationRepository.save(participation);
        return EventParticipationResponse.toEventParticipation(updated, null);
    }

    public EventParticipationResponse getParticipationById(UUID userId, UUID eventId) {
        log.info("Getting participation By Id");
        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"));
        System.out.println(participation.getUser().getId() + " " + participation.getEvent().getId());

        return EventParticipationResponse.toEventParticipation(participation, null);
    }

    public Page<EventParticipationResponse> getAllParticipations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventParticipation> participations = eventParticipationRepository.findAll(pageable);

        return participations.map(participation -> new EventParticipationResponse(
                participation.getId(),
                participation.getUser() != null ? participation.getUser().getId() : null,
                participation.getEvent().getId(),
                participation.getStatusPaymentEventParticipation(),
                participation.getCommunity(),
                participation.getName(),
                participation.getEmail(),
                participation.getPhoneNumber(),
                participation.getDateBirth(),
                participation.getIsMarried(),
                participation.getHasParticipated(),
                participation.getUserFunctionInEvent(),
                null
        ));
    }

    public Page<UserInEventInfosResponse> getAllParticipationsByEventId(UUID eventId, Pageable pageable) {
        Page<EventParticipation> participations = eventParticipationRepository.findAllByEvent_Id(eventId, pageable);
        List<UserInEventInfosResponse> users = new ArrayList<>();
        for (EventParticipation participation : participations) {
            users.add(new UserInEventInfosResponse().toResponse(participation, users.size()));
        }
        return participations.map(part -> new UserInEventInfosResponse().toResponse(part, users.size()));
    }

    public Page<UserInEventWithEventInfosResponse> getAllUserParticipationByUserId(UUID userId, Pageable pageable) {

        Page<EventParticipation> participations = eventParticipationRepository.
                findAllByUser_IdAndEvent_DateGreaterThanEqualOrderByEvent_DateAsc(userId, LocalDateTime.now(),
                        pageable);

        if (participations.isEmpty()) {
            Page<EventParticipation> allByUserId = this.eventParticipationRepository.findAllByUser_IdOrderByEvent_Title(
                    userId, pageable);

            participations = allByUserId;
        }

        return participations.map(p -> new UserInEventWithEventInfosResponse().toResponse(p));
    }

    public EventParticipationResponse addFunctionUserInEvent(UUID userId, UUID eventId, UserFunctionInEvent function) {
        EventParticipation eventParticipation = this.eventParticipationRepository.findByUser_IdAndEvent_Id(userId,
                        eventId)
                .orElseThrow(
                        () -> new EventParticipationException(
                                "event.id.not.found")
                );
        eventParticipation.setUserFunctionInEvent(function);
        eventParticipationRepository.save(eventParticipation);

        return EventParticipationResponse.toEventParticipation(eventParticipation, null);
    }

    public UserEventStatus getStatusParticipationOfEvent(UUID userId, UUID eventId) {
        Optional<EventParticipation> eventParticipation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId,
                eventId);
        if (eventParticipation.isEmpty()) {
            return new UserEventStatus(userId, UserEventStatusTypes.NAO_PARTICIPA,
                    StatusPaymentEventParticipation.NAO_PAGO, null);
        }
        EventParticipation participation = eventParticipation.get();
        if (participation.getPaidDate() == null) {
            return new UserEventStatus(userId, UserEventStatusTypes.PENDENTE, StatusPaymentEventParticipation.PENDENTE,
                    null);
        }
        return new UserEventStatus(userId, UserEventStatusTypes.PARTICIPANTE, StatusPaymentEventParticipation.PAGO,
                participation.getPaidDate());
    }

    public Page<UserEventStatus> getStatusPayementParticipationOfEvent(UUID eventId,
                                                                       StatusPaymentEventParticipation status,
                                                                       Pageable pageable) {
        Page<EventParticipation> allByEventIdAndStatusPaymentEventParticipation =
                this.eventParticipationRepository.findAllByEvent_IdAndStatusPaymentEventParticipation(eventId, status
                        , pageable);

        if (allByEventIdAndStatusPaymentEventParticipation.isEmpty()) {
            throw new EventIllegalArumentException("The list of status " + status + " is empty");
        }

        return allByEventIdAndStatusPaymentEventParticipation.map(participation -> new UserEventStatus(
                participation.getId(),
                UserEventStatusTypes.PARTICIPANTE,
                participation.getStatusPaymentEventParticipation(),
                participation.getPaidDate()
        ));
    }

    public EventParticipation getUserParticipationInEventByUserId(UUID userId, UUID eventId) {
        return eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException("participation.event.not.found"));
    }

    public EventParticipationResponse addParticipateAsAdminService(EventParticipationAdmDTO dto) {
        log.info("Add participant as Admin");
        EventParticipation eventParticipation = new EventParticipation();
        Optional<User> optionalUser =
                (dto.getUserId() != null) ? userRepository.findById(dto.getUserId()) : Optional.empty();

        Optional<EventInvite> eventInviteOptional = this.eventInviteRepository.findById(dto.getEventInviteId());

        Event event = eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found",
                        dto.getEventId().toString()));
        eventParticipation.setCommunity(dto.getCommunity());
        eventParticipation.setEvent(event);
        eventParticipation.setUserFunctionInEvent(dto.getUserFunctionInEvent());
        eventParticipation.setStatusPaymentEventParticipation(dto.getStatusPayment());
        optionalUser.ifPresent(eventParticipation::setUser);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            eventParticipation.setName(user.getName());
            eventParticipation.setEmail(user.getEmail());
            if (user.getPhoneNumber() != null) {
                eventParticipation.setPhoneNumber(user.getPhoneNumber());
            } else {
                eventParticipation.setPhoneNumber(dto.getPhoneNumber());
            }
            if (user.getDateBirth() != null) {
                java.util.Date utilDate = user.getDateBirth();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                LocalDate localDate = sqlDate.toLocalDate();
                OffsetDateTime birthDateAtUtc = localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
                eventParticipation.setDateBirth(birthDateAtUtc);
            } else {
                eventParticipation.setDateBirth(dto.getDateBirth().toInstant().atOffset(ZoneOffset.UTC));
            }
        } else {
            eventParticipation.setName(dto.getName());
            eventParticipation.setEmail(dto.getEmail());
            eventParticipation.setPhoneNumber(dto.getPhoneNumber());
            eventParticipation.setDateBirth(dto.getDateBirth().toInstant().atOffset(ZoneOffset.UTC));
        }
        eventParticipation.setIsMarried(dto.getIsMarried());
        eventParticipation.setHasParticipated(dto.getStatusPayment() == StatusPaymentEventParticipation.PAGO);
        eventParticipation.setPaidDate(dto.getStatusPayment() == StatusPaymentEventParticipation.PAGO ? Instant.now()
                .atOffset(ZoneOffset.UTC) : null);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setCpf("05961055256");
            if (user.getCpf() != null && dto.getStatusPayment() == StatusPaymentEventParticipation.PENDENTE && eventInviteOptional.isPresent()) {
                EventInvite eventInvite = eventInviteOptional.get();
                CreatePixPaymentRequestDTO paymentRequestDTO = new CreatePixPaymentRequestDTO(
                        BigDecimal.valueOf(eventInvite.getValue()),
                        event.getTitle(),
                        user.getEmail(),
                        "",
                        "",
                        user.getCpf()
                );

                try {

                    Payment createPaymentUser = mercadoPagoClient.createPixPayment(paymentRequestDTO, user.getId());
                    eventParticipation.setMercadoPagoPaymentId(createPaymentUser.getId());
                } catch (MPApiException apiException) {
                    log.error("Erro na API do Mercado Pago. Status: {}, Mensagem: {}. Detalhes: {}",
                            apiException.getStatusCode(),
                            apiException.getMessage(),
                            apiException.getApiResponse().getContent());
                    throw new MercadoPagoAPIException("Erro ao criar pagamento Pix. Por favor, tente novamente: " +
                            apiException.getMessage());
                } catch (MPException | RuntimeException e) {
                    log.error("Erro ao criar pagamento Pix no Mercado Pago: {}", e.getMessage(), e);
                    throw new MercadoPagoException("Erro ao criar pagamento Pix. Por favor, tente novamente: " +
                            e.getMessage());
                }
            }
        }
        return EventParticipationResponse.toEventParticipation(eventParticipationRepository.save(eventParticipation),
                null);
    }

    public UserPaymentDetailResponse getUserPaymentDetail(UUID userId, UUID eventId) {
        log.info("Getting user payment detail");
        Event event = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        EventParticipation participation = this.eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException("participation.event.not.found"));
        double valuePaid = 0;
        String comprovant = "";
        String pdfBase64 = "";

        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO
                && !event.getItsFree()) {
            valuePaid = participation.getAmountPaid();

            // Busca a transação associada para pegar o comprovante
            if (participation.getMercadoPagoPaymentId() != null) {
                try {
                    comprovant = mercadoPagoClient.generateBase64ReceiptPayment(
                            participation.getMercadoPagoPaymentId(), user);
                    byte[] pdfBytes = mercadoPagoClient.generatePDFReceiptPayment(
                            participation.getMercadoPagoPaymentId(),
                            user);

                    pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
                } catch (MPException e) {
                    throw new MercadoPagoException("Erro no cliente do mercado pago: " + e.getMessage());
                } catch (MPApiException e) {
                    throw new MercadoPagoAPIException("Erro na API do mercado pago: " + e.getMessage());
                } catch (IOException e) {
                    throw new GenerateReceiptException(
                            "Erro gerando o comprovante de pagamento do usuário: " + e.getMessage());
                }
            }
        }

        return new UserPaymentDetailResponse(eventId, userId, event.getTitle(), user.getName(), valuePaid,
                participation.getEventInviteAssociated().getValue(),
                participation.getPaidDate(),
                participation.getStatusPaymentEventParticipation(), comprovant, pdfBase64);
    }

    public Page<User> listUsersNotParticipateOfEvent(UUID eventId, Pageable page) {
        log.info("Listing users not participate of event {}", eventId);
        return this.eventParticipationRepository.listUsersWhoNotParticipateOfEvent(eventId, page);
    }

    public Page<User> listUsersNotParticipateOfEventByName(UUID eventId, String name, Pageable page) {
        return this.eventParticipationRepository.listUsersWhoNotParticipateOfEventByName(eventId, name, page);
    }


    public Page<UserInEventInfosResponse> getAllParticipationsByNameOFEventId(String name, UUID eventId,
                                                                              Pageable page) {
        Page<EventParticipation> participations = eventParticipationRepository.listAllByUser_nameAndEvent_id(name,
                eventId, page);
        return participations.map(part -> new UserInEventInfosResponse().toResponse(part, 0));
    }

    public Page<UserInEventInfosResponse> getAllParticipationsByFilterOfEvent(
            StatusPaymentEventParticipation statusPaymentEventParticipation, UUID eventId, Pageable page) {
        Page<EventParticipation> participations = eventParticipationRepository.findAllByEvent_IdAndStatusPaymentEventParticipation(
                eventId, statusPaymentEventParticipation, page);
        return participations.map(part -> new UserInEventInfosResponse().toResponse(part, 0));
    }

    public String listUsersAsPdf(UUID eventId, StatusPaymentEventParticipation status) {
        List<EventParticipation> participations;
        Event event = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new EventParticipationException("event.not.found"));

        // LÓGICA DE FILTRAGEM
        if (status != null) {
            // Se um status foi fornecido, use o método de filtro
            participations = eventParticipationRepository.findAllByEvent_IdAndStatusPaymentEventParticipation(eventId, status);
        } else {
            // Caso contrário, pegue todos
            participations = eventParticipationRepository.findAllByEvent_Id(eventId);
        }
        String pdfBase64 = "";
        if (participations.isEmpty()) {
            throw new EventParticipationException("participation.event.not.found");
        }
        try {
            pdfBase64 = Base64.getEncoder().encodeToString(
                    this.pdfGenerationService.generatePdfFromParticipationsInevent(participations, event));
        } catch (IOException e) {
            throw new GenerateParticipationsPDFException("Não foi possivel gerar o PDF: " + e.getMessage());
        }
        return pdfBase64;
    }

    public Boolean validateHasFrenteCaixa(UUID userId, UUID eventId) {
        return this.eventParticipationRepository.isFrenteCaixa(userId, eventId, UserFunctionInEvent.FRENTE_CAIXA)
                .isPresent();
    }

    public Boolean removeParticipant(UUID participantId) {
        EventParticipation eventParticipation = this.eventParticipationRepository.findById(participantId)
                .orElseThrow(() -> new EventParticipationException("participation.event.not.found"));
        this.eventParticipationRepository.delete(eventParticipation);
        return true;
    }


}