package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventScaleIllegalArgumentException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.exception.MinistryListEmptyException;
import br.hallel.relational.api.app.ministry.exception.RoleMinistryNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.MinistryInterface;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.repository.MinistryMemberRoleRepository;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import br.hallel.relational.api.app.ministry.repository.RoleMinistryRepository;
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
import java.util.ArrayList;
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
    @Autowired
    private RoleMinistryRepository roleMinistryRepository;

    @Autowired
    private MinistryMemberRoleRepository ministryMemberRoleRepository;

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
                .orElseThrow(() -> new UserNotFoundException(
                        "User to add as coordinator not found by id: %s".formatted(ministryRequestDTO.getCoordinatorId()
                                .toString())));
        User viceCoordinator = userRepository.findById(ministryRequestDTO.getViceCoordinatorId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User to add as vice-coordinator not found by id: %s".formatted(
                                ministryRequestDTO.getViceCoordinatorId()
                                        .toString())));
        ministryMapped.setCoordinator(coordinator);
        ministryMapped.setViceCoordinator(viceCoordinator);
        Ministry ministry =
                this.ministryRepository.save(ministryMapped);
        String imageUrl = null;


        imageUrl = googleBucketService.sendFileToBucket(
                image, GoogleBucketUtils.getImageName(
                        ministry.getId()
                                .toString(), Ministry.class.getSimpleName(), "image"
                )
        );

        ministry.setImage(imageUrl);
        log.info("Ministry Response: " + ministry.toString());

        log.info("Adding coordinator and vice-coordinator as members ministry");
        List<RoleMinistry> roleMinistryList = roleMinistryRepository.findAll();
        addCoordinatorToMemberMinistryTable(ministry.getCoordinator().getId(), ministry.getId(),
                roleMinistryList.stream().filter(roleMinistry -> roleMinistry.getDescription().equals("COORDINATOR"))
                        .toList().get(0));
        addCoordinatorToMemberMinistryTable(ministry.getViceCoordinator().getId(), ministry.getId(),
                roleMinistryList.stream()
                        .filter(roleMinistry -> roleMinistry.getDescription().equals("VICE_COORDINATOR"))
                        .toList().get(0));

        return mapper.entityMinistryToResponse(this.ministryRepository.save(ministry));
    }

    public List<MinistryResponse> listAllMinistries() {
        List<Ministry> ministryList = this.ministryRepository.findAll();
        List<MinistryResponse> responseList =
                ministryList.stream().map(ministry ->
                                mapper.entityMinistryToResponse(ministry))
                        .collect(Collectors.toList());

        return responseList;
    }

    @Override
    public List<MinistryResponse> listAllMinistriesPage(int page,
                                                        int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ministry> ministryPagination = this.ministryRepository.findAll(pageable);

        List<MinistryResponse> responseList =
                ministryPagination.stream().map(ministry ->
                                mapper.entityMinistryToResponse(ministry))
                        .collect(Collectors.toList());
        if (responseList.isEmpty()) {
            throw new MinistryListEmptyException(
                    "The ministry list is empty. You should create One");
        }
        log.info("Listing ministries...", responseList);
        return responseList;
    }

    @Override
    public MinistryResponse getMinistryById(UUID id) {
        log.info("Getting ministry by id" + id);

        Ministry ministry = this.ministryRepository.findById(id)
                .orElseThrow(() -> new MinistryIllegalArgumentException("Ministry Id: " + id + " not found!"));

        log.info("Ministry Response: " + ministry.getId());

        return mapper.entityMinistryToResponse(ministry);
    }

    @Override
    public MinistryResponse editMinistry(UUID id,
                                         MinistryRequestDTO ministryRequestDTO,
                                         MultipartFile image) {
        log.info("Updating ministry: {}", id);
        Ministry ministry = ministryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ministério não encontrado"));

        ministry.setTitle(ministryRequestDTO.getTitle());
        ministry.setDescription(ministryRequestDTO.getDescription());
        ministry.setMinistryType(ministryRequestDTO.getMinistryType());
        ministry.setHasRepertoire(ministryRequestDTO.getHasRepertoire());

        if (image != null) {
            log.info("has image");

            String imageUrl = googleBucketService.updateFileOfBucket(
                    image,
                    GoogleBucketUtils.getImageName(
                            ministry.getId().toString(),
                            Ministry.class.getSimpleName(),
                            "image"
                    )
            );
            ministry.setImage(imageUrl);
            log.info("image Url Response: " + imageUrl);

        }
        List<RoleMinistry> roleMinistryList = roleMinistryRepository.findAll();

        if (ministryRequestDTO.getCoordinatorId() != null
                && !ministryRequestDTO.getCoordinatorId().equals(ministry.getCoordinator().getId())) {
            deleteCoordinatorFromMemberMinistryTable(ministry.getCoordinator().getId(), ministry.getId());

            addCoordinatorToMemberMinistryTable(ministryRequestDTO.getCoordinatorId(), ministry.getId(),
                    roleMinistryList.stream()
                            .filter(roleMinistry -> roleMinistry.getDescription().equals("COORDINATOR"))
                            .toList().get(0));

            User newCoordinator = userRepository.findById(ministryRequestDTO.getCoordinatorId())
                    .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));
            ministry.setCoordinator(newCoordinator);
        }

        if (ministryRequestDTO.getViceCoordinatorId() != null
                && !ministryRequestDTO.getViceCoordinatorId().equals(ministry.getViceCoordinator().getId())) {
            deleteCoordinatorFromMemberMinistryTable(ministry.getViceCoordinator().getId(), ministry.getId());
            addCoordinatorToMemberMinistryTable(ministryRequestDTO.getViceCoordinatorId(), ministry.getId(),
                    roleMinistryList.stream()
                            .filter(roleMinistry -> roleMinistry.getDescription().equals("VICE_COORDINATOR"))
                            .toList().get(0));

            User newViceCoordinator = userRepository.findById(ministryRequestDTO.getViceCoordinatorId())
                    .orElseThrow(() -> new RuntimeException("Vice Coordenador não encontrado"));
            ministry.setViceCoordinator(newViceCoordinator);
        }

        return mapper.entityMinistryToResponse(ministryRepository.save(ministry));
    }

    private void deleteCoordinatorFromMemberMinistryTable(UUID userId, UUID ministryId) {
        log.info("Deleting coordinator from member ministry table...");

        Optional<MemberMinistry> optionalMemberMinistry = this.memberMinistryRepository.findMemberMinistryByUser_IdAndMinistry_Id(
                userId, ministryId);

        if (optionalMemberMinistry.isEmpty()) {
            throw new MemberMinistryRegisterNotFoundException(
                    "Member Ministry Id: " + userId + " not found as member ministry!");
        }
        MemberMinistry oldMinistryMember = optionalMemberMinistry.get();
        memberMinistryRepository.delete(oldMinistryMember);
    }

    private void addCoordinatorToMemberMinistryTable(UUID userId, UUID ministryId, RoleMinistry coordinatorLevel) {
        log.info("Adding coordinator to member ministry table...");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new RuntimeException("Ministry not found with id: " + ministryId));

        MemberMinistry memberMinistrySaved = memberMinistryRepository.save(new MemberMinistry(user, ministry));


        List<RoleMinistry> rolesMinistry = roleMinistryRepository.findAll();

        RoleMinistry roleMember = rolesMinistry.stream()
                .filter(roleMinistry -> roleMinistry.getDescription().equals("MEMBER")).findFirst()
                .orElseThrow(() -> new RoleMinistryNotFoundException("Role ministry not found"));
        ministryMemberRoleRepository.save(new MinistryMemberRole(
                new MinistryMemberRoleIds(memberMinistrySaved.getId(), coordinatorLevel.getId())));
        ministryMemberRoleRepository.save(new MinistryMemberRole(
                new MinistryMemberRoleIds(memberMinistrySaved.getId(), roleMember.getId())));
    }

    @Override
    public MinistryResponse deleteMinistryById(UUID id) {
        Ministry ministry = mapper.responseToEntity(this.getMinistryById(id));
        googleBucketService.deleteFileOfBucket(ministry.getImage());
        log.info("Image deleted from bucket...");
        this.ministryRepository.deleteById(id);

        return mapper.entityMinistryToResponse(ministry);
    }

    @Override
    public List<EventScaleResponse> listAllEventScalesByMinistryId(UUID ministryId) {
        return this.eventScaleMapper.listEntityToResponse(this.ministryRepository.
                findAllEventScalesByMinistryId(ministryId));
    }

    @Override
    public List<EventScaleSimpleResponse> listAllEventScalesByMinistryIdAndRangeDate(UUID ministryId,
                                                                                     LocalDateTime startDate,
                                                                                     LocalDateTime endDate) {

        if (startDate.isAfter(endDate)) {
            throw new EventScaleIllegalArgumentException("Data inicial não pode ser maior que a data final.");
        }
        List<EventScaleSimpleResponse> responses = new ArrayList<>();
        List<EventScale> eventScales = this.ministryRepository.findAllEventScalesByMinistryIdAndDateRange(ministryId,
                startDate, endDate);
        for (EventScale eventScale : eventScales) {
            responses.add(new EventScaleSimpleResponse(eventScale.getId(),
                    eventScale.getDate()));
        }
        log.info("Getting events Scales By MInistry!");
        return responses;
    }

    public boolean validateCoordinatorOfMinistry(UUID ministryId, UUID userId) {
        List<MemberMinistry> membersMinistries = memberMinistryRepository.findMemberMinistriesByMinistry_Id(ministryId);
        for (MemberMinistry memberMinistry : membersMinistries) {
            if (memberMinistry.getUser().getId().equals(userId)) {
                List<String> rolesString = memberMinistry.getMinistryRoles().stream().map(RoleMinistry::getDescription)
                        .toList();

                if (rolesString.contains("COORDINATOR")) {
                    return true;
                }
                if (rolesString.contains("VICE_COORDINATOR")) {
                    return true;
                }
                if (rolesString.contains("EXTERNAL_COORDINATOR")) {
                    return true;
                }
            }
        }
        return false;
    }


    public StatusParticipationMinistry listStatusParticipationInMinistry(UUID ministryId, UUID userId) {
        Ministry ministry = this.ministryRepository.findById(ministryId)
                .orElseThrow(() -> new MinistryIllegalArgumentException(
                        "Ministry not found by id %s".formatted(ministryId)));

        if (ministry.getCoordinator().getId().equals(userId)) {
            return StatusParticipationMinistry.COORDINATOR;
        }
        if (ministry.getViceCoordinator().getId().equals(userId)) {
            return StatusParticipationMinistry.VICE_COORDINATOR;
        }
        return StatusParticipationMinistry.MEMBER;

    }

    public List<MinistryResponse> listAllMinistriesByTitleOrderByAsc(String title) {
        List<Ministry> ministries = this.ministryRepository.findAllByTitleContainingIgnoreCaseOrderByTitle(title);

        return this.mapper.entityMinistriesToResponse(ministries);
    }

}

