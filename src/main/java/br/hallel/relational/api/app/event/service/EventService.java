package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventHomePageResponse;
import br.hallel.relational.api.app.event.exception.EventoIllegalArumentException;
import br.hallel.relational.api.app.event.interfaces.EventInterface;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.MinistryScale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
public class EventService implements EventInterface {

    @Autowired
    private EventRepository repository;

    @Override
    public EventDTO create(EventDTO eventDTO, MultipartFile fileImage, MultipartFile fileBanner) {
        if (eventDTO.title() == null || eventDTO.title()
                .isEmpty()) {
            throw new EventoIllegalArumentException("Não foi possível criar o evento.");
        }
        log.info("Creating evento...");
        Event event = this.repository.save(eventDTO.toEvent(eventDTO));
//        if ((fileImage != null && !(fileImage.isEmpty())) && (fileBanner != null && !(fileBanner.isEmpty()))) {
//
//            String imageUrl = null;
//            String bannerImageUrl = null;
//            try {
//                imageUrl = bucketService.sendImageToBucket(fileImage, GoogleBucketUtils
//                        .getImageName(
//                                eventos.getId(),
//                                Eventos.class.getSimpleName(),
//                                "image"));
//                bannerImageUrl = bucketService.sendImageToBucket(fileBanner, GoogleBucketUtils
//                        .getImageName(
//                                eventos.getId(),
//                                Eventos.class.getSimpleName(),
//                                "banner"));
//
//                event.setFileImageUrl(imageUrl);
//                event.setBanner(bannerImageUrl);
//                log.info(event.getFileImageUrl());
//                log.info(event.getBanner());
//                event = this.repository.save(eventos);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

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

        return eventDTO;
}

@Override
public List<EventHomePageResponse> listEventsToHomePage() {
    List<EventHomePageResponse> listResponse = new ArrayList<>();
    for (Event event : repository.findAll()) {
        listResponse.add(new EventHomePageResponse().toEventResponse(event));
    }
    return listResponse;
}

@Override
public EventHomePageResponse getEventById(UUID id) {
    Optional<Event> optional = this.repository.findById(id);
    log.info("Getting event...");
    if(!optional.isPresent()) {
    log.info("Event not found...");
        return null;
    }

    return new EventHomePageResponse()
            .toEventResponse(optional.get());
}

@Override
public EventDTO updateById(UUID id, EventDTO eventDTO) {
    return null;
}

@Override
public EventDTO deleteById(UUID id) {
    return null;
}

@Override
public List<EventHomePageResponse> listEventsByTitleAsc() {
    return List.of();
}

@Override
public List<EventHomePageResponse> listEventsByDateExp() {
    return List.of();
}
}
