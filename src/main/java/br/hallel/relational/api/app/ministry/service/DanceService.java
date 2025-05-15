package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.DanceAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.DanceResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.dto.mapper.RepertoryMapper;
import br.hallel.relational.api.app.ministry.exception.RepertoryNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.DanceInterface;
import br.hallel.relational.api.app.ministry.model.DanceMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.DanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DanceService implements DanceInterface {

    @Autowired
    private DanceRepository repository;
    @Autowired
    private MinistryService ministryService;
    private final RepertoryMapper mapper;
    private final MinistryMapper ministryMapper;

    public DanceService(RepertoryMapper repertoryMapper, MinistryMapper ministryMapper) {
        this.mapper = repertoryMapper;
        this.ministryMapper = ministryMapper;
    }

    @Override
    public DanceResponse createDance(DanceAddEditDTO danceDTO) {
        log.info("Creating dance...");
        DanceMinistry danceModel = new DanceMinistry();
        Ministry ministry = ministryMapper.
                responseToEntity(this.ministryService.getMinistryById(danceDTO.getMinistry()));
        danceModel.setName(danceDTO.getName());
        danceModel.setDescription(danceDTO.getDescription());
        danceModel.setLink(danceDTO.getLink());
        danceModel.setMinistry(ministry);
        DanceMinistry dance = this.repository.save(danceModel);
        log.info("Dance " + dance.getId() + " created!");
        return mapper.danceEntityToResponse(dance);
    }

    @Override
    public DanceResponse getDanceById(UUID id) {
        Optional<DanceMinistry> danceOptional = this.repository.findById(id);
        if (danceOptional.isEmpty()) {
            throw new RepertoryNotFoundException("Dance Id: " + id + " not found!");
        }
        return mapper.danceEntityToResponse(danceOptional.get());
    }

    @Override
    public void deleteDanceById(UUID id) {
        log.info("Deleting dance... {}", id);
        DanceMinistry dance = mapper.danceResponseToEntity(this.getDanceById(id));

        log.info("Dance {} deleted!", id);
        this.repository.delete(dance);
    }

    @Override
    public List<DanceResponse> listAllDances() {
        return mapper.toListDanceResponse(this.repository.findAll());
    }
}
