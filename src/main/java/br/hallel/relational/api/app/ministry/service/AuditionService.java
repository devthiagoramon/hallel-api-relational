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
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.MemberAuditionMinistry;
import br.hallel.relational.api.app.ministry.repository.AuditionRepository;
import br.hallel.relational.api.app.ministry.repository.MemberAuditionMinistryRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public AuditionResponse createAudition(AuditionDTO auditionDTO, UUID memberAuditionId) {
        log.info("Scale: " + auditionDTO.getEventScale().toString());
        log.info("Ministry: " + auditionDTO.getMinistry().toString());
        AuditionMinistry auditionMinistry = new AuditionMinistry();
        auditionMinistry.setTitle(auditionDTO.getTitle());
        auditionMinistry.setDescription(auditionDTO.getDescription());
        auditionMinistry.setDate(auditionDTO.getDate());
        auditionMinistry.setEventScale(
                eventScaleMapper.responseToEntity(
                        this.eventScaleService.getEventScaleById(auditionDTO.getEventScale()))
        );
        auditionMinistry.setMinistry(
                this.ministryMapper.responseToEntity(
                        this.ministryService.getMinistryById(auditionDTO.getMinistry())
                )
        );

        AuditionMinistry saved = this.repository.save(auditionMinistry);
        if (memberAuditionId !=null){
        User user = userRepository.findById(memberAuditionId).orElseThrow(() ->
                new UserNotFoundException("User not found"));
            this.memberAuditionMinistryRepository.save(
                    new MemberAuditionMinistry(
                            MemberEventScaleStatus.CONVIDADO,
                            user,
                            saved
                    )
            );
            log.info("Audition: Membro que criou, já está participando"
                    + auditionDTO.getEventScale().toString());
        }

        return this.auditionMapper.entityToResponse(saved);
    }

    public AuditionResponse getAuditionById(UUID id) {
        return this.auditionMapper.entityToResponse(this.repository.findById(id).orElseThrow());
    }

    public void deleteAuditionById(UUID id) {
        this.repository.deleteById(id);
    }

    public AuditionResponse updateAuditionById(UUID id, AuditionDTO auditionDTO) {
        log.info(auditionDTO.toString());
        AuditionMinistry audition = this.repository.findById(id).orElseThrow();
        audition.setTitle(auditionDTO.getTitle());
        audition.setDescription(auditionDTO.getDescription());
        audition.setDate(auditionDTO.getDate());
        EventScaleResponse eventScale = this.eventScaleService.getEventScaleById(auditionDTO.getEventScale());
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

    public List<EventScaleSimpleResponse> listScalesThatCanAssociateIntoEventScale(
            UUID idMinisterio,
            LocalDateTime from) {
        log.info("Listing escalas that can associate into ensaio of ministerio {}...", idMinisterio);

        return this.repository.findAllEventScalesThatCanBeAddedIntoAudition(idMinisterio, from);
    }

}
