package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventScaleIllegalArgumentException;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.interfaces.MinistryInterface;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
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
    private final EventScaleMapper eventScaleMapper;

    public MinistryService(MinistryMapper ministryMapper, EventScaleMapper eventScaleMapper) {
        this.mapper = ministryMapper;
        this.eventScaleMapper = eventScaleMapper;
    }

    @Override
    public MinistryResponse createMinistry(
            MinistryRequestDTO ministryRequestDTO,
            MultipartFile image) {
        log.info("Ministry RequestDTO: " + ministryRequestDTO.toString());

        if (image == null) {
            throw new MinistryIllegalArgumentException("Ministry image can't be null!");
        }

        Ministry ministryMapped = mapper.requestToEntity(ministryRequestDTO);

        User coordinator = userRepository.findById(ministryRequestDTO.getCoordinatorId())
                .orElseThrow(() -> new UserNotFoundException("User to add as coordinator not found by id: %s".formatted(ministryRequestDTO.getCoordinatorId()
                        .toString())));
        User viceCoordinator = userRepository.findById(ministryRequestDTO.getViceCoordinatorId())
                .orElseThrow(() -> new UserNotFoundException("User to add as vice-coordinator not found by id: %s".formatted(ministryRequestDTO.getViceCoordinatorId()
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
        addCoordinatorToMemberMinistryTable(ministry.getCoordinatorId().getId(), ministry.getId());
        addCoordinatorToMemberMinistryTable(ministry.getViceCoordinatorId().getId(), ministry.getId());

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
        log.info("Getting ministry by id" + id);

        Ministry ministry = this.ministryRepository.findById(id).orElseThrow(() -> new MinistryIllegalArgumentException("Ministry Id: " + id + " not found!"));

        log.info("Ministry Response: " + ministry.getId());

        return mapper.entityMinistryToResponse(ministry);
    }

    @Override
    public MinistryResponse editMinistry(UUID id,
                                         MinistryRequestDTO ministryRequestDTO,
                                         MultipartFile image) {
        log.info("Updating ministry: {}", id);
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

        if (ministryRequestDTO.getCoordinatorId() != null
                && ministryRequestDTO.getCoordinatorId() != ministry.getCoordinatorId().getId()) {
            deleteCoordinatorFromMemberMinistryTable(ministry.getCoordinatorId().getId(), ministry.getId());
            addCoordinatorToMemberMinistryTable(ministryRequestDTO.getCoordinatorId(), ministry.getId());
        }
        if (ministryRequestDTO.getViceCoordinatorId() != null
                && ministryRequestDTO.getViceCoordinatorId() != ministry.getViceCoordinatorId().getId()) {
            deleteCoordinatorFromMemberMinistryTable(ministry.getViceCoordinatorId().getId(), ministry.getId());
            addCoordinatorToMemberMinistryTable(ministry.getViceCoordinatorId().getId(), ministry.getId());
        }

        return mapper.entityMinistryToResponse(this.ministryRepository.save(ministry));
    }

    private void deleteCoordinatorFromMemberMinistryTable(UUID userId, UUID ministryId) {
        log.info("Deleting coordinator from member ministry table...");
        Optional<MemberMinistry> optionalMemberMinistry = memberMinistryRepository.findById(new MemberMinistryId(userId, ministryId));
        if (optionalMemberMinistry.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException("Member Ministry Id: " + userId + " not found as member ministry!");
        }
        MemberMinistry oldMinistryMember = optionalMemberMinistry.get();
        memberMinistryRepository.delete(oldMinistryMember);
    }

    private void addCoordinatorToMemberMinistryTable(UUID userId, UUID ministryId) {
        log.info("Adding coordinator to member ministry table...");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new RuntimeException("Ministry not found with id: " + ministryId));

        MemberMinistryId memberMinistryId = new MemberMinistryId(userId, ministryId);

        memberMinistryRepository.save(new MemberMinistry(memberMinistryId, user, ministry));
    }

    @Override
    public MinistryResponse deleteMinistryById(UUID id) {
        Ministry ministry = mapper.responseToEntity(this.getMinistryById(id));
        try {
            googleBucketService.deleteImageOfBucket(ministry.getImage());
            log.info("Image deleted from bucket...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.ministryRepository.deleteById(id);

        return mapper.entityMinistryToResponse(ministry);
    }

    @Override
    public List<EventScaleResponse> listAllEventScalesByMinistryId(UUID ministryId) {
        return this.eventScaleMapper.listEntityToResponse(this.ministryRepository.
                findAllEventScalesByMinistryId(ministryId));
    }

    @Override
    public List<EventScaleResponse> listAllEventScalesByMinistryIdAndRangeDate(UUID ministryId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new EventScaleIllegalArgumentException("Data inicial não pode ser maior que a data final.");
        }
        return this.eventScaleMapper.listEntityToResponse(
                this.ministryRepository.findAllEventScalesByMinistryIdAndDateRange(ministryId, startDate, endDate)
        );
    }

    public boolean validateCoordinatorOfMinistry(UUID ministryId, UUID userId) {
        Ministry ministry = this.ministryRepository.findById(ministryId).orElseThrow(() -> new MinistryIllegalArgumentException("Can't find ministry of id %s".formatted(ministryId)));

        if (ministry.getCoordinatorId().getId().equals(userId)) return true;
        return ministry.getViceCoordinatorId().getId().equals(userId);
    }
}
