package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.interfaces.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.interfaces.MinistryInterface;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
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

@Slf4j
@Service
public class MinistryService implements MinistryInterface {

    @Autowired
    private MinistryRepository ministryRepository;
    @Autowired
    private GoogleBucketService googleBucketService;
    @Autowired
    private MemberMinistryRepository memberMinistryRepository;
    @Autowired
    private UserRepository userRepository;

    private final MinistryMapper mapper;

    public MinistryService(MinistryMapper Ministrymapper) {
        this.mapper = Ministrymapper;
    }

    @Override
    public MinistryResponse createMinistry(
            MinistryRequestDTO ministryRequestDTO,
            MultipartFile image) {
        log.info("Ministry RequestDTO: " + ministryRequestDTO.toString());

        if (image != null) {
            throw new MinistryIllegalArgumentException("Ministry image can't be null!");
        }

        Ministry ministryMapped = mapper.requestToEntity(ministryRequestDTO);

        User coordinator = userRepository.findById(ministryRequestDTO.getCoordinatorId())
                                         .orElseThrow(() -> new UserNotFoundException("User to add as coordinator not found by id: %s".formatted(ministryRequestDTO.getCoordinatorId()
                                                                                                                                                                   .toString())));
        User viceCoordinator = userRepository.findById(ministryRequestDTO.getCoordinatorId())
                                             .orElseThrow(() -> new UserNotFoundException("User to add as vice-coordinator not found by id: %s".formatted(ministryRequestDTO.getCoordinatorId()
                                                                                                                                                                            .toString())));
        ministryMapped.setCoordinatorId(coordinator);
        ministryMapped.setViceCoordinatorId(viceCoordinator);
        Ministry ministry =
                this.ministryRepository.save(ministryMapped);
        String imageUrl = null;

        try {
            imageUrl = googleBucketService.sendImageToBucket(
                    image, GoogleBucketUtils.getImageName(
                            ministry.getId()
                                    .toString(), Ministry.class.getSimpleName(), "image"
                                                         )
                                                            );
        } catch (IOException e) {
            log.info("Image Url Response: " + image);
            throw new RuntimeException(e);
        }
        ministry.setImage(imageUrl);
        log.info("Ministry Response: " + ministry.toString());

        log.info("Adding coordinator and vice-coordinator as members ministry");
        MemberMinistryId coordinatorMinistry = new MemberMinistryId(ministry.getCoordinatorId()
                                                                            .getId(), ministry.getId());
        MemberMinistryId viceCoordinatorMinistry = new MemberMinistryId(ministry.getViceCoordinatorId()
                                                                                .getId(), ministry.getId());

        memberMinistryRepository.saveAll(List.of(
                new MemberMinistry(coordinatorMinistry),
                new MemberMinistry(viceCoordinatorMinistry)
                                                ));

        return mapper.entityMinistryToResponse(this.ministryRepository.save(ministry));
    }

    @Override
    public List<MinistryResponse> listAllMinistries(int page,
                                                    int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ministry> ministryPagination = this.ministryRepository.findAll(pageable);

        List<MinistryResponse> responseList =
                ministryPagination.stream().map(ministry ->
                                          mapper.entityMinistryToResponse(ministry))
                                  .collect(Collectors.toList());

        log.info("Listing ministries...", responseList);
        return responseList;
    }

    @Override
    public MinistryResponse getMinistryById(UUID id) {
        Optional<Ministry> optional = this.ministryRepository.findById(id);
        if (!optional.isPresent()) {
            throw new MinistryIllegalArgumentException("Ministry Id: " + id + " not found!");
        }
        log.info("id" + id);
        log.info("Ministry Response: " + optional.get());

        return mapper.entityMinistryToResponse(optional.get());
    }

    @Override
    public MinistryResponse editMinistry(UUID id,
                                         MinistryRequestDTO ministryRequestDTO,
                                         MultipartFile image) {
        Ministry ministry = mapper.responseToEntity(this.getMinistryById(id));

        ministry.setId(id);
        ministry.setTitle(ministryRequestDTO.getTitle());
        ministry.setDescription(ministryRequestDTO.getDescription());
        ministry.setMinistryType(ministryRequestDTO.getMinistryType());
        ministry.setHasRepertoire(ministryRequestDTO.getHasRepertoire());

        if (image != null) {
            log.info("has image");
            String imageUrl = null;
            try {
                imageUrl = googleBucketService.updateImageOfBucket(
                        image, GoogleBucketUtils.getImageName(
                                ministry.getId()
                                        .toString(), Ministry.class.getSimpleName(), "image"
                                                             ));
                ministry.setImage(imageUrl);
                log.info("image Url Response: " + imageUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return mapper.entityMinistryToResponse(this.ministryRepository.save(ministry));
    }

    @Override
    public void deleteMinistryById(UUID id) {
        this.getMinistryById(id);
        log.info("Deleting Ministry by Id: " + id);
        this.ministryRepository.deleteById(id);
    }
}
