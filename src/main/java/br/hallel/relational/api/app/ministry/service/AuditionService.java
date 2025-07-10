package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.event.service.EventScaleService;
import br.hallel.relational.api.app.ministry.dto.AuditionDTO;
import br.hallel.relational.api.app.ministry.dto.AuditionResponse;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.AuditionMapper;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.exception.MemberAuditionMinistryNotFound;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.MemberAuditionMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.repository.AuditionRepository;
import br.hallel.relational.api.app.ministry.repository.MemberAuditionMinistryRepository;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuditionService {

    @Autowired
    private AuditionRepository repository;
    @Autowired
    private EventScaleService eventScaleService;
    @Autowired
    private MinistryService ministryService;

    @Autowired
    private AuditionMapper auditionMapper;
    @Autowired
    private EventScaleMapper eventScaleMapper;
    @Autowired
    private MinistryMapper ministryMapper;
    @Autowired
    private MemberAuditionMinistryRepository memberAuditionMinistryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberMinistryRepository memberMinistryRepository;

    public AuditionResponse createAudition(AuditionDTO auditionDTO, UUID memberAuditionId) {
        log.info("Scale: " + auditionDTO.getEventScale());
        log.info("Ministry: " + auditionDTO.getMinistry());
        AuditionMinistry auditionMinistry = new AuditionMinistry();
        auditionMinistry.setTitle(auditionDTO.getTitle());
        auditionMinistry.setDescription(auditionDTO.getDescription());
        auditionMinistry.setDate(auditionDTO.getDate());
        if (auditionDTO.getEventScale() != null) {
            auditionMinistry.setEventScale(
                    eventScaleMapper.responseToEntity(
                            this.eventScaleService.getEventScaleById(auditionDTO.getEventScale()))
            );
        }

        auditionMinistry.setMinistry(
                this.ministryMapper.responseToEntity(
                        this.ministryService.getMinistryById(auditionDTO.getMinistry())
                )
        );

        AuditionMinistry saved = this.repository.save(auditionMinistry);
        if (memberAuditionId != null) {
            Optional<MemberMinistry> memberMinistry =
                    memberMinistryRepository.
                            findMemberMinistryByUser_IdAndMinistry_Id(memberAuditionId, auditionDTO.getMinistry());
            if (memberMinistry.isEmpty()) {
                throw new
                        MemberMinistryRegisterNotFoundException
                        ("Member ministry not registred wih id %s".formatted(memberAuditionId));
            }
            this.memberAuditionMinistryRepository.save(
                    new MemberAuditionMinistry(
                            MemberEventScaleStatus.CONVIDADO,
                            memberMinistry.get(),
                            saved
                    )
            );
            log.info("Audition: Membro que criou, já está participando"
                    + auditionDTO.getEventScale());
        }

        return this.auditionMapper.entityToResponse(saved);
    }

    public AuditionResponse getAuditionById(UUID id) {
        return this.auditionMapper.entityToResponse(this.repository.findById(id).orElseThrow());
    }

    @Transactional
    public void deleteAuditionById(UUID id) {
        AuditionMinistry entity = repository.findById(id)
                .orElseThrow(() -> new MemberAuditionMinistryNotFound("Audition not found"));

        this.memberAuditionMinistryRepository.deleteAllByAuditionMinistryId(id);
        this.repository.delete(entity);
    }

    public AuditionResponse updateAuditionById(UUID id, AuditionDTO auditionDTO) {
        log.info(auditionDTO.toString());
        AuditionMinistry audition = this.repository.findById(id).orElseThrow();
        audition.setTitle(auditionDTO.getTitle());
        audition.setDescription(auditionDTO.getDescription());
        audition.setDate(auditionDTO.getDate());
        EventScaleResponse eventScale = null;
        if (auditionDTO.getEventScale() != null) {
            eventScale = this.eventScaleService.getEventScaleById(auditionDTO.getEventScale());
        }
        MinistryResponse ministryResponse = this.ministryService.getMinistryById(auditionDTO.getMinistry());
        audition.setEventScale(this.eventScaleMapper.responseToEntity(eventScale));
        audition.setMinistry(this.ministryMapper.responseToEntity(ministryResponse));
        return this.auditionMapper.entityToResponse(this.repository.save(audition));
    }

    public List<AuditionResponse> listAllAuditions() {
        return this.auditionMapper.toListResponse(this.repository.findAll());
    }

    public List<AuditionResponse> listAllAuditionsByMinistryId(UUID ministryId) {
        List<AuditionMinistry> allByMinistryId = this.repository.findAllByMinistry_Id(ministryId);
        if (allByMinistryId.isEmpty()) {
            throw new MinistryIllegalArgumentException("Auditions in Ministry id is empty");
        }

        for (AuditionMinistry auditionMinistry : allByMinistryId) {
            log.info("Audition: {}", auditionMinistry.getTitle());
        }

        return this.auditionMapper.toListResponse(allByMinistryId);
    }

    public AuditionResponse getAuditionByMinistryId(UUID ministryId) {
        AuditionMinistry ministryRes = this.repository.findByMinistry_Id(ministryId);
        if (ministryRes == null) {
            throw new MinistryIllegalArgumentException("Auditions in Ministry id is empty");
        }
        return this.auditionMapper.entityToResponse(ministryRes);
    }

    public List<EventScaleSimpleResponse> listScalesThatCanAssociateIntoEventScale(
            UUID idMinisterio,
            LocalDateTime from) {
        log.info("Listing escalas that can associate into ensaio of ministerio {}...", idMinisterio);

        return this.repository.findAllEventScalesThatCanBeAddedIntoAudition(idMinisterio, from);
    }

}
