package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.dto.mapper.EventMapper;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.interfaces.EventInterface;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.ministry.model.Ministry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService implements EventInterface {

    @Autowired
    private EventRepository repository;
    @Autowired
    private GoogleBucketService bucketService;

    private final EventMapper mapper;

    public EventService(EventMapper eventMapper) {
        this.mapper = eventMapper;
    }

    @Override
    public EventResponse create(EventDTO eventDTO, MultipartFile fileImage, MultipartFile fileBanner) {
        if (eventDTO.title() == null
                || eventDTO.description() == null
                || eventDTO.date() == null) {

            throw new EventIllegalArumentException("Não foi possível criar o evento. Preencha os campos corretamente!");
        }

        log.info("Creating evento...");
        Event event = this.repository.save(mapper.dtoToEntity(eventDTO));

        if ((fileImage != null && !(fileImage.isEmpty()))
                && (fileBanner != null && !(fileBanner.isEmpty()))) {

            String imageUrl = null;
            String bannerImageUrl = null;
            try {
                imageUrl = bucketService.sendImageToBucket(fileImage, GoogleBucketUtils
                        .getImageName(
                                event.getId().toString(),
                                Event.class.getSimpleName(),
                                "image"));

                bannerImageUrl = bucketService.sendImageToBucket(fileBanner, GoogleBucketUtils
                        .getImageName(
                                event.getId().toString(),
                                Event.class.getSimpleName(),
                                "banner"));

                event.setImage_url(imageUrl);
                event.setBanner_url(bannerImageUrl);
                log.info(event.getImage_url());
                log.info(event.getBanner_url());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


//        try {
//            if (event.getMinistriesAssociated() != null && !event.getMinistriesAssociated()
//                    .isEmpty()) {
//
//                for (Ministry ministry : event.getMinistriesAssociated()) {
//                    MinistryScale ministryScale = new MinistryScale();
//                    if (event.getMinistriesScales() == null) {
//                        List<MinistryScale> ministriesScales = new ArrayList<>();
//                        ministriesScales.add(ministryScale);
//                        event.setMinistriesScales(ministriesScales);
//                    } else {
//                        event.getMinistriesScales().add(ministryScale);
//                        event = this.repository.save(event);
//                    }
//                }
//            }
//        } catch (Exception e) {
//
//        }

        return mapper.entityToResponse(this.repository.save(event));
    }

    @Override
    public List<EventResponse> listEventsToHomePage(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAll(pageable);

        List<EventResponse> listResponse =
                eventsPagination.stream().map(event -> mapper.entityToResponse(event)).collect(Collectors.toList());
        log.info("Listing events...", listResponse);
        return listResponse;
    }

    @Override
    public EventResponse getEventById(UUID id) {
        Optional<Event> optional = this.repository.findById(id);


        if (!optional.isPresent()) {
            log.info("Event not found...");
            throw new EventIllegalArumentException("Evento de ID" + id + " não encontrado!");
        }

        return mapper.entityToResponse(optional.get());
    }

    @Override
    public EventResponse updateById(UUID id, EventDTO eventDTO, MultipartFile img_url, MultipartFile banner_url) {
        Event event = this.repository.findById(id).
                orElseThrow(() -> new EventIllegalArumentException("Evento não encontrado!"));

        event.setId(id);
        event.setTitle(eventDTO.title());
        event.setDescription(eventDTO.description());
        event.setDate(eventDTO.date());
        event.setLocal_event_name(eventDTO.local_event_name());
        event.setLocal_event_latitude(eventDTO.local_event_latitude());
        event.setLocal_event_longitude(eventDTO.local_event_longitude());
        event.setIsImportant(eventDTO.isImportant());

        if (img_url != null && banner_url != null) {
            log.info("has image");
            String imageUrl = null, bannerUrl = null;
            try {
                imageUrl = bucketService.updateImageOfBucket(
                        img_url, GoogleBucketUtils.getImageName(
                                event.getId()
                                        .toString(), Ministry.class.getSimpleName(), "image"
                        ));
                bannerUrl = bucketService.updateImageOfBucket(
                        banner_url, GoogleBucketUtils.getImageName(
                                event.getId()
                                        .toString(), Ministry.class.getSimpleName(), "banner"
                        ));
                event.setImage_url(imageUrl);
                event.setBanner_url(bannerUrl);
                log.info("image Url Response: " + imageUrl);
                log.info("image Url Response: " + bannerUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            event.setBanner_url(event.getBanner_url());
            event.setImage_url(event.getImage_url());
        }

        log.info("Updating event... id: " + id);
        return mapper.entityToResponse(this.repository.save(event));
    }

    @Override
    public Boolean deleteById(UUID id) {
        Event event = mapper.responseToEntity(this.getEventById(id));
        try {
            this.bucketService.deleteImageOfBucket(event.getImage_url());
            this.bucketService.deleteImageOfBucket(event.getBanner_url());
            log.info("Image and banner deleted from bucket...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.repository.deleteById(id);
        return true;
    }

    @Override
    public EventShortResponse listEventInScaleInfo(UUID id) {
        log.info("Listing evento in escala info by id {}", id);
        Optional<EventShortResponse> optional = this.repository.findByIdShort(id);
        if (optional.isEmpty()) {
            throw new EventIllegalArumentException("Can't find evento by this id");
        }

        return optional.get();
    }

    @Override
    public List<EventResponse> listEventsByTitleAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAllByOrderByTitleAsc(pageable);

        List<EventResponse> listResponse =
                eventsPagination.stream().map(event -> mapper.entityToResponse(event)).collect(Collectors.toList());
        log.info("Listing events Order by title ASC...", listResponse);
        return listResponse;

    }

    @Override
    public List<EventResponse> listEventsByDateExp(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPagination = this.repository.findAllByOrderByDateAsc(pageable);

        List<EventResponse> listResponse =
                eventsPagination.stream().map(event -> mapper.entityToResponse(event)).collect(Collectors.toList());
        log.info("Listing events Order By Data expiration...", listResponse);
        return listResponse;

    }


}
