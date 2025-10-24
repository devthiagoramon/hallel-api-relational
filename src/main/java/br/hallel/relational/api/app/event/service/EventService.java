package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.EventMapper;
import br.hallel.relational.api.app.event.exception.*;
import br.hallel.relational.api.app.event.interfaces.EventInterface;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.event.repository.LimitEventAgeGroupRepository;
import br.hallel.relational.api.app.global.pdf.PdfGenerationService;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.global.utils.LocalDateTimeUtils;
import br.hallel.relational.api.app.global.utils.NumberUtils;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventService implements EventInterface {

    private final EventRepository repository;
    private final GoogleBucketService bucketService;
    private final MinistryRepository ministryRepository;
    private final EventScaleService eventScaleService;
    private final EventTransactionRepository eventTransactionRepository;

    private final EventMapper mapper;
    private final MinistryMapper ministryMapper;
    private final PdfGenerationService pdfGenerationService;

    private final LimitEventAgeGroupRepository limitEventAgeGroupRepository;

    @Override
    public EventResponse create(EventDTO eventDTO,
                                MultipartFile fileImage,
                                MultipartFile fileBanner) {
        log.info("Creating event...");

        if (eventDTO.getDate().before(new Date())) {
            throw new EventIllegalArumentException("Não foi possivel criar o evento: Data inválida!");
        }

        if (eventDTO.getTitle() == null
                || eventDTO.getDescription() == null
                || eventDTO.getDate() == null) {

            throw new EventIllegalArumentException("Não foi possível criar o evento. Preencha os campos corretamente!");
        }
        log.info(eventDTO.getMinistryIds().toString());
        Double value = NumberUtils.extrairEConverterParaDouble(eventDTO.getValue());
        boolean itsFreeValue = value == 0;
        Event eventToSave = mapper.dtoToEntity(eventDTO);

        eventToSave.setEventType(eventDTO.getEventType());

        eventToSave.setValue(value);
        eventToSave.setItsFree(itsFreeValue);
        eventToSave.setIsImportant(eventDTO.getIsImportant());
        eventToSave.setDuration(eventDTO.getDuration());
        eventToSave.setImage_url("");
        eventToSave.setBanner_url("");
        eventToSave.setEventStatus(eventDTO.getDate().after(new Date()) ? EventStatus.AGENDADO : EventStatus.OCORRENDO);
        Event event = this.repository.save(eventToSave);
        generateLimiteAgeGroupForEvent(event);
        if ((fileImage != null && !(fileImage.isEmpty()))
                && (fileBanner != null && !(fileBanner.isEmpty()))) {

            String imageUrl = null;
            String bannerImageUrl = null;

            imageUrl = bucketService.sendFileToBucket(fileImage, GoogleBucketUtils
                    .getImageName(
                            eventToSave.getId().toString(),
                            Event.class.getSimpleName(),
                            "image"));

            bannerImageUrl = bucketService.sendFileToBucket(fileBanner, GoogleBucketUtils
                    .getImageName(
                            eventToSave.getId().toString(),
                            Event.class.getSimpleName(),
                            "banner"));

            event.setImage_url(imageUrl);
            event.setBanner_url(bannerImageUrl);
            log.info(eventToSave.getImage_url());
            log.info(eventToSave.getBanner_url());

        }
        event = this.repository.save(eventToSave);

        for (UUID ministryId : eventDTO.getMinistryIds()) {
            log.info("Creating event scale in event {} with ministry {}", event.getId(), ministryId);
            eventScaleService.createScale(event, ministryId);
        }

        return mapper.entityToResponse(this.repository.save(event));
    }


    @Override
    public Page<EventResponse> listAllEvents(Pageable pageable) {
        Page<Event> eventsPage = this.repository.findAll(pageable);

        List<EventResponse> eventResponses = eventsPage.getContent().stream()
                .map(this::eventToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(eventResponses, pageable, eventsPage.getTotalElements());
    }

    public Page<EventResponse> listAllEventsByEventStatus(Pageable pageable, EventStatus eventStatus) {
        log.info("Listing events by event status {}", eventStatus);
        Page<Event> event = this.repository.findByEventStatus(eventStatus, pageable);
        List<EventResponse> eventResponses =
                event.stream().map(this::eventToResponse).collect(Collectors.toList());
        return new PageImpl<>(eventResponses, pageable, event.getTotalElements());
    }

    public Page<EventResponse> listAllEventsHomePage(Pageable pageable) {
        log.info("List all Events");

        Page<Event> events = this.repository.findByEventStatusNotOrderByDateAsc(EventStatus.FINALIZADO, pageable);
        List<EventResponse> eventResponses = events.getContent().stream().map(this::eventToResponse)
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            log.info("All events has status 'FINALIZADO', listing ALL EVENTS");
            return this.listAllEvents(pageable);
        }

        return new PageImpl<>(eventResponses, pageable, events.getTotalElements());
    }

    public Page<EventResponse> listAllRetreatsAlreadyHappened(Pageable pageable) {

        Page<Event> eventsPagination = this.repository.findAllByEventStatusAndEventType(EventStatus.FINALIZADO,
                EventType.RETIRO,
                pageable);
        if (eventsPagination.isEmpty()) {
            throw new EventListIsEmptyException("event.list.is.empty");
        }
        List<EventResponse> sortedEventResponses = eventsPagination.stream()
                .map(mapper::entityToResponse)
                .sorted(Comparator.comparing(EventResponse::getTitle)).toList();

        log.info("Listing all events...");
        return new PageImpl<>(sortedEventResponses, pageable, eventsPagination.getTotalElements());
    }

    public Page<EventResponse> listAllRetreats(Pageable pageable) {

        Page<Event> eventsPagination = this.repository.findAllByEventTypeOrderByTitleAsc(EventType.RETIRO, pageable);
        log.info("Listing all retreats...");
        if (eventsPagination.isEmpty()) {
            throw new EventIllegalArumentException(
                    "Nenhum retiro encontrado! Talvez você não tenha criado nenhum ainda...");
        }

        return eventsPagination.map(mapper::entityToResponse);
    }

    @Override
    public EventResponseWithMinistryAssociated getEventById(UUID id) {
        log.info("Getting event by id {}", id);

        Optional<Event> op = this.repository.listByIdWithMinistryResponse(id);
        if (op.isEmpty()) {
            throw new EventNotFoundException("event.id.not.found", id.toString());
        }
        Event event = op.get();
        List<MinistryResponse> ministriesAssociated = event.getScales().stream().map((scale) -> {
            Ministry ministry = scale.getMinistry();
            return ministryMapper.entityMinistryToResponse(ministry);
        }).toList();

        EventResponseWithMinistryAssociated eventResponse = mapper.eventToResponseWithMinistryAssociated(event);
        eventResponse.setMinistries(ministriesAssociated);
        return eventResponse;

    }

    @Override
    public EventResponse updateById(UUID id, EventDTO eventDTO,
                                    MultipartFile img_url,
                                    MultipartFile banner_url) {
        Event event = this.repository.findById(id).
                orElseThrow(() -> new EventIllegalArumentException("Evento não encontrado!"));

        if (!eventDTO.getDate().equals(event.getDate())) {
            this.eventScaleService.editEventDate(event.getId(), eventDTO.getDate());
        }
        event.setId(id);
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setDate(eventDTO.getDate());
        event.setDuration(eventDTO.getDuration());
        event.setLocal_event_name(eventDTO.getLocal_event_name());
        event.setLocal_event_latitude(eventDTO.getLocal_event_latitude());
        event.setLocal_event_longitude(eventDTO.getLocal_event_longitude());
        event.setIsImportant(eventDTO.getIsImportant());
        event.setEventType(eventDTO.getEventType());
        Double value = NumberUtils.extrairEConverterParaDouble(eventDTO.getValue());
        boolean itsFreeValue = value == 0;
        event.setItsFree(itsFreeValue);
        event.setValue(value);
        if (img_url != null) {
            log.info("Editing image {}", img_url.getOriginalFilename());
            String imageUrl = null;
            imageUrl = bucketService.updateFileOfBucket(
                    img_url, GoogleBucketUtils.getImageName(
                            event.getId().toString()
                            , Ministry.class.getSimpleName(), "image"
                    ));
            event.setImage_url(imageUrl);
        }
        if (banner_url != null) {
            log.info("Editing banner {}", banner_url.getOriginalFilename());
            String bannerUrl = null;
            bannerUrl = bucketService.updateFileOfBucket(
                    banner_url, GoogleBucketUtils.getImageName(
                            event.getId().toString()
                            , Ministry.class.getSimpleName(), "banner"
                    ));
            event.setBanner_url(bannerUrl);
        } else {
            event.setBanner_url(event.getBanner_url());
            event.setImage_url(event.getImage_url());
        }


        log.info("Updating event... id: " + id);
        return mapper.entityToResponse(this.repository.save(event));
    }

    @Override
    public Boolean deleteById(UUID id) {
        Event event = this.repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("event.id.not.found", id.toString()));
        log.info("Image and banner deleted from bucket...");
        this.repository.deleteById(id);

        this.bucketService.deleteFileOfBucket(event.getImage_url());
        this.bucketService.deleteFileOfBucket(event.getBanner_url());
        return true;
    }

    @Override
    public EventShortResponse listEventInScaleInfo(UUID id) {
        log.info("Listing evento in escala info by id {}", id);
        Optional<EventShortResponse> optional = this.repository.findByIdShort(id);
        if (optional.isEmpty()) {
            throw new EventNotFoundException("event.id.not.found", id.toString());
        }

        return optional.get();
    }

    @Override
    public List<EventResponse> listEventsByTitleOrderByAsc(int page,
                                                           int size, EventStatus eventStatus) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPagination;
        if (eventStatus == null) {
            eventsPagination = this.repository.findAllByOrderByTitleAsc(pageable);
        } else {
            eventsPagination = this.repository.findByEventStatusOrderByTitleAsc(eventStatus, pageable);
        }

//        if (eventsPagination.isEmpty()) {
//            eventsPagination = this.repository.findAllByOrderByTitleAsc(pageable);
//        }

        List<EventResponse> listResponse =
                eventsPagination.stream()
                        .map(mapper::entityToResponse)
                        .toList();
        log.info("Listing events Order by title ASC...", listResponse);
        return listResponse;

    }

    @Override
    public List<EventResponse> listEventsByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Event> events = this.repository.findAllByTitleContainingIgnoreCaseOrderByTitleAsc(title, pageable);

        return events.stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    @Override
    public Page<EventResponse> listEventsByDateExp(Pageable pageable) {
        Page<Event> eventsPagination = this.repository.findAllByDateGreaterThanEqualOrderByDateAsc(LocalDateTime.now(),
                pageable);

        if (eventsPagination.isEmpty()) {
            throw new EventListIsEmptyException("event.list.is.empty");
        }

        return eventsPagination.map(mapper::entityToResponse);

    }

    public List<EventSimpleResponse> listAllEventsByMinistryId(UUID ministryId) {
        return this.ministryRepository.findAllEventsByMinistryId(ministryId);
    }

    public Page<EventResponse> listAllUpcomingEvents(int page, int size) {
        log.info("Listing upcoming events by actual date");
        Pageable pageable = PageRequest.of(page, size);
        return this.repository.findAllUpcomingEvents(new Date(), pageable).map(mapper::entityToResponse);
    }

    public EventTransactionResponse addTransaction(EventTransactionDTO dto) {
        Event event = this.repository.findById(dto.eventID()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.eventID().toString())
        );

        EventTransaction eventTransaction = new EventTransaction();
        eventTransaction.setDescription(dto.desciption());
        eventTransaction.setTransactionType(dto.transactionType());
        eventTransaction.setDateTransaction(dto.dateTransaction());
        eventTransaction.setValue(dto.value());
        eventTransaction.setEvent(event);
        eventTransaction.setIsEditable(true);
        EventTransaction save = this.eventTransactionRepository.save(eventTransaction);
        return new EventTransactionResponse().toResponse(save);
    }

    public Page<EventTransactionResponse> listAllTransactionsByEvent(UUID eventId, Pageable pageable) {
        this.repository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );

        Page<EventTransaction> list = eventTransactionRepository.findByEventId(eventId, pageable);

        if (list.isEmpty()) {
            throw new EventTransactionEmptyListException("event.transaction.list.is.empty", eventId.toString());
        }

        return list.map(eventTransaction -> new EventTransactionResponse().toResponse(eventTransaction));
    }

    public Page<EventTransactionResponse> listAllTransactionsByEventAndTransactionType(UUID eventId,
                                                                                       TransactionType type,
                                                                                       Pageable pageable) {
        return eventTransactionRepository.findByEventIdAndTransactionType(eventId, type, pageable)
                .map(eventTransaction -> new EventTransactionResponse().toResponse(eventTransaction));

    }

    public EventTransactionResponse getTransactionById(UUID transactionId) {
        return new EventTransactionResponse().toResponse(
                eventTransactionRepository.findById(transactionId).orElseThrow(
                        () -> new EventTransactionNotFoundException("event.transaction.not.found",
                                transactionId.toString())
                )
        );
    }

    public List<EventTransactionResponse> listAllTransactions() {
        return this.eventTransactionRepository.findAll().stream().map(
                t -> new EventTransactionResponse().toResponse(t)
        ).toList();
    }

    public EventTransactionResponse findTransactionById(UUID transactionId) {
        EventTransaction transaction = eventTransactionRepository.findById(transactionId).orElseThrow(
                () -> new EventTransactionNotFoundException("event.transaction.not.found", transactionId.toString()));
        return new EventTransactionResponse().toResponse(transaction);
    }

    public EventTransactionResponse updateTransaction(UUID id, EventTransactionDTO dto) {
        EventTransaction transaction = eventTransactionRepository.findById(id)
                .orElseThrow(() -> new EventTransactionNotFoundException("event.transaction.not.found", id.toString()));

        transaction.setDescription(dto.desciption());
        transaction.setTransactionType(dto.transactionType());
        transaction.setDateTransaction(dto.dateTransaction());
        transaction.setValue(dto.value());

        EventTransaction saved = eventTransactionRepository.save(transaction);
        return new EventTransactionResponse().toResponse(saved);
    }

    public void deleteTransaction(UUID id) {
        if (!eventTransactionRepository.existsById(id)) {
            throw new EventTransactionNotFoundException("event.transaction.not.found", id.toString());
        }
        eventTransactionRepository.deleteById(id);
    }

    public EventBalanceResponse getBalance(UUID eventId) {
        Event event = this.repository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );

        List<EventTransaction> transactions = this.eventTransactionRepository.findAllByEvent_Id(eventId);

        if (transactions.isEmpty()) {
            throw new EventTransactionEmptyListException("event.transaction.list.is.empty", eventId.toString());
        }

        double inputAmount = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.ENTRADA)
                .mapToDouble(EventTransaction::getValue)
                .sum();

        double outputAmount = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.SAIDA)
                .mapToDouble(EventTransaction::getValue)
                .sum();

        double total = inputAmount - outputAmount;

        return new EventBalanceResponse(eventId, event.getEventType(), inputAmount, outputAmount, total,
                total >= 0 ? BalanceType.LUCRO : BalanceType.PREJUIZO);
    }

    public List<EventCashFlowResponse> getEventCashFlow(UUID eventId) {
        repository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );

        List<EventTransaction> allTransactions =
                eventTransactionRepository.findAllByEvent_Id(eventId);

        if (allTransactions.isEmpty()) {
            throw new EventIllegalArumentException("The list of cash flow is empty");
        }

        List<EventCashFlowResponse> response = new ArrayList<>();

        Map<Date, List<EventTransaction>> transactionsFilteredByDate = allTransactions.stream()
                .collect(
                        Collectors.groupingBy(EventTransaction::getDateTransaction, TreeMap::new, Collectors.toList()));


        transactionsFilteredByDate.forEach((dateTransaction, eventTransactions) -> {
            Double totalProfit = eventTransactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.ENTRADA)
                    .mapToDouble(EventTransaction::getValue).reduce(0.0, Double::sum);
            Double totalExpense = eventTransactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.SAIDA)
                    .mapToDouble(EventTransaction::getValue).reduce(0.0, Double::sum);
            response.add(new EventCashFlowResponse(eventId, totalProfit, totalExpense, totalProfit - totalExpense,
                    LocalDateTime.ofInstant(dateTransaction.toInstant(), ZoneId.of(LocalDateTimeUtils.MANAUS_ZONE_ID)),
                    allTransactions.stream().map(EventTransactionResponse::toResponse).toList()));
        });

        return response;

    }

    private EventResponse eventToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getBanner_url(),
                event.getImage_url(),
                event.getIsImportant(),
                event.getLocal_event_name(),
                event.getLocal_event_longitude(),
                event.getLocal_event_latitude(),
                event.getValue(),
                null,
                event.getEventType(),
                event.getEventStatus()
        );
    }

    public String getTransactionEventPDF(UUID eventId, TransactionType filter) {

        Event event = this.repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("event.id.not.found", eventId.toString()));
        List<EventTransaction> transactions = this.eventTransactionRepository.findAllByEvent_Id(eventId);

        if (filter != null) {
            transactions = transactions.stream().filter(t -> t.getTransactionType() == filter).toList();
        }

        Double balance = 0.0;
        Double incomings = 0.0;
        Double outgoings = 0.0;

        if (!transactions.isEmpty()) {
            for (EventTransaction transaction : transactions) {
                if (transaction.getTransactionType() == TransactionType.ENTRADA) {
                    balance += transaction.getValue();
                    incomings += transaction.getValue();
                } else if (transaction.getTransactionType() == TransactionType.SAIDA) {
                    balance -= transaction.getValue();
                    outgoings += transaction.getValue();
                }
            }
        }
        String pdfBase64;
        try {
            pdfBase64 = Base64.getEncoder()
                    .encodeToString(
                            this.pdfGenerationService.generatePDFTransactionsEvents(event, transactions, filter,
                                    balance,
                                    incomings, outgoings));
        } catch (IOException e) {
            throw new GenerateEventTransactionPDFException("Não foi possivel gerar o PDF de transações");
        }
        return pdfBase64;
    }

    public void generateLimiteAgeGroupForEvent(Event event) {
        log.info("Generating limit age group for event {}", event.getId());
        for (AgeGroup ageGroup : AgeGroup.values()) {
            LimitEventAgeGroup limit = new LimitEventAgeGroup();

            if (ageGroup.name() == AgeGroup.CRIANÇA.name()) {
                limit.setLimitQuantity(60);
            } else if (ageGroup.name() == AgeGroup.TEEN.name()) {
                limit.setLimitQuantity(30);
            } else if (ageGroup.name() == AgeGroup.JOVEM.name()) {
                limit.setLimitQuantity(20);
            } else if (ageGroup.name() == AgeGroup.ADULTO.name()) {
                limit.setLimitQuantity(20);
            }
            limit.setAgeGroup(ageGroup);
            limit.setCurrentQuantity(0);
            limit.setEvent(event);
            this.limitEventAgeGroupRepository.save(
                    limit
            );
        }
    }
}
