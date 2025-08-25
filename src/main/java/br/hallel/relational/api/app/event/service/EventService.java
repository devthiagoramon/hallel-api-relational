package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.model.EventType;
import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.EventMapper;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventNotFoundException;
import br.hallel.relational.api.app.event.exception.EventTransactionNotFoundException;
import br.hallel.relational.api.app.event.interfaces.EventInterface;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.TransactionType;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.global.utils.NumberUtils;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional

public class EventService implements EventInterface {

    @Autowired
    private EventRepository repository;
    @Autowired
    private GoogleBucketService bucketService;
    @Autowired
    private MinistryRepository ministryRepository;
    @Autowired
    private EventScaleService eventScaleService;
    @Autowired
    private EventTransactionRepository eventTransactionRepository;

    private final EventMapper mapper;
    private final MinistryMapper ministryMapper;

    public EventService(EventMapper eventMapper, MinistryMapper ministryMapper) {
        this.mapper = eventMapper;
        this.ministryMapper = ministryMapper;
    }

    @Override
    public EventResponse create(EventDTO eventDTO,
                                MultipartFile fileImage,
                                MultipartFile fileBanner) {
        log.info("Creating event...");
        if (eventDTO.getTitle() == null
                || eventDTO.getDescription() == null
                || eventDTO.getDate() == null) {

            throw new EventIllegalArumentException("Não foi possível criar o evento. Preencha os campos corretamente!");
        }
        log.info(eventDTO.getMinistryIds().toString());
        Double value = NumberUtils.extrairEConverterParaDouble(eventDTO.getValue());
        boolean itsFreeValue = value == 0;
        Event eventToSave = mapper.dtoToEntity(eventDTO);

        eventToSave.setHasEnded(false);
        eventToSave.setEventType(eventDTO.getEventType());

        eventToSave.setValue(value);
        eventToSave.setItsFree(itsFreeValue);
        eventToSave.setIsImportant(eventDTO.getIsImportant());
        eventToSave.setImage_url("");
        eventToSave.setBanner_url("");
        Event event = this.repository.save(eventToSave);

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
    public Page<EventResponse> listAllEvents(int page,
                                             int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAllByOrderByTitleAsc(pageable);
        log.info("Listing events...");
        return eventsPagination.map(mapper::entityToResponse);
    }

    public Page<EventResponse> listAllRetreats(int page,
                                             int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAllByEventTypeOrderByTitleAsc(EventType.RETIRO,pageable);
        log.info("Listing all retreats...");
        if (eventsPagination.isEmpty()){
            throw new EventIllegalArumentException("No Retreats created. Maybe you need create onde");
        }

        return eventsPagination.map(mapper::entityToResponse);
    }

    @Override
    public EventResponseWithMinistryAssociated getEventById(UUID id) {
        log.info("Getting event by id {}", id);
        Event event = this.repository.listByIdWithMinistryResponse(id)
                .orElseThrow(() -> new EventIllegalArumentException("Event id %s not found".formatted(id.toString())));
        List<MinistryResponse> ministriesAssociated = event.getScales().stream().map((scale) -> {
            Ministry ministry = scale.getMinistry();
            return ministryMapper.entityMinistryToResponse(ministry);
        }).collect(Collectors.toList());
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
        event.setLocal_event_name(eventDTO.getLocal_event_name());
        event.setLocal_event_latitude(eventDTO.getLocal_event_latitude());
        event.setLocal_event_longitude(eventDTO.getLocal_event_longitude());
        event.setIsImportant(eventDTO.getIsImportant());
        event.setEventType(eventDTO.getEventType());
        Double value = NumberUtils.extrairEConverterParaDouble(eventDTO.getValue());
        boolean itsFreeValue = value == 0;
        event.setItsFree(itsFreeValue);
        event.setValue(value);
        if (img_url != null ){
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
                .orElseThrow(() -> new EventIllegalArumentException("Event id %s not found".formatted(id.toString())));
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
            throw new EventIllegalArumentException("Can't find event by this id");
        }

        return optional.get();
    }

    @Override
    public List<EventResponse> listEventsByTitleOrderByAsc(int page,
                                                           int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAllByOrderByTitleAsc(pageable);

        List<EventResponse> listResponse =
                eventsPagination.stream()
                        .map(event -> mapper.entityToResponse(event))
                        .collect(Collectors.toList());
        log.info("Listing events Order by title ASC...", listResponse);
        return listResponse;

    }

    @Override
    public List<EventResponse> listEventsByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Event> events = this.repository.findAllByTitleContainingIgnoreCaseOrderByTitleAsc(title, pageable);

        return events.stream()
                .map(event -> mapper.entityToResponse(event))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> listEventsByDateExp(int page,
                                                   int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAllByOrderByDateAsc(pageable);

        List<EventResponse> listResponse =
                eventsPagination.stream()
                        .map(event -> mapper.entityToResponse(event))
                        .collect(Collectors.toList());
        log.info("Listing events Order By Data expiration...", listResponse);
        return listResponse;

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
                () -> new EventNotFoundException("Event id %s not found".formatted(dto.eventID()))
        );

        EventTransaction eventTransaction = new EventTransaction();
        eventTransaction.setDesciption(dto.desciption());
        eventTransaction.setTransactionType(dto.transactionType());
        eventTransaction.setDateTransaction(dto.dateTransaction());
        eventTransaction.setValue(dto.value());
        eventTransaction.setEvent(event);
        EventTransaction save = this.eventTransactionRepository.save(eventTransaction);
        return new EventTransactionResponse().toResponse(save);
    }

    public List<EventTransactionResponse> listAllTransactionsByEvent(UUID eventId) {
        this.repository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event id %s not found".formatted(eventId))
        );

        List<EventTransactionResponse> list = eventTransactionRepository.findByEventId(eventId)
                .stream()
                .map(tx -> new EventTransactionResponse().toResponse(tx))
                .toList();

        if(list.isEmpty()){
            throw new EventIllegalArumentException("Can't find event transaction by this id "+eventId);
        }

        return list;
    }
    public List<EventTransactionResponse> listAllTransactionsByEventAndTransactionType(UUID eventId, TransactionType type) {
        return eventTransactionRepository.findByEventIdAndTransactionType(eventId, type)
                .stream()
                .map(tx -> new EventTransactionResponse().toResponse(tx))
                .toList();
    }

    public EventTransactionResponse getTransactionById(UUID transactionId) {
        return new EventTransactionResponse().toResponse(
                eventTransactionRepository.findById(transactionId).orElseThrow(
                        () -> new EventNotFoundException("Transaction id %s not found".formatted(transactionId.toString()))
                )
        );
    }
    public List<EventTransactionResponse> listAllTransactions() {
        return this.eventTransactionRepository.findAll().stream().map(
                t -> new EventTransactionResponse().toResponse(t)
        ).collect(Collectors.toList());
    }

    public EventTransactionResponse findTransactionById(UUID transactionId) {
        EventTransaction transaction = eventTransactionRepository.findById(transactionId).orElseThrow(
                () -> new EventTransactionNotFoundException(
                        "Transaction id %s not found".formatted(transactionId.toString()))
        );
        return new EventTransactionResponse().toResponse(transaction);
    }

    public EventTransactionResponse updateTransaction(UUID id, EventTransactionDTO dto) {
        EventTransaction transaction = eventTransactionRepository.findById(id)
                .orElseThrow(() -> new EventTransactionNotFoundException("Transaction not found"));

        transaction.setDesciption(dto.desciption());
        transaction.setTransactionType(dto.transactionType());
        transaction.setDateTransaction(dto.dateTransaction());
        transaction.setValue(dto.value());

        EventTransaction saved = eventTransactionRepository.save(transaction);
        return new EventTransactionResponse().toResponse(saved);
    }

    public void deleteTransaction(UUID id) {
        if (!eventTransactionRepository.existsById(id)) {
            throw new EventTransactionNotFoundException("Transaction not found");
        }
        eventTransactionRepository.deleteById(id);
    }
}
