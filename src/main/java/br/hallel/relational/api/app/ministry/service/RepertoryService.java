package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.repository.EventScaleRepository;

import br.hallel.relational.api.app.ministry.dto.*;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.dto.mapper.RepertoryMapper;
import br.hallel.relational.api.app.ministry.exception.ListRepertoryEmptyException;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.exception.RepertoryNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.RepertoryInterface;
import br.hallel.relational.api.app.ministry.model.*;
import br.hallel.relational.api.app.ministry.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service

public class RepertoryService implements RepertoryInterface {

    @Autowired
    private RepertoryRepository repository;
    @Autowired
    private EventScaleRepository scaleRepository;
    @Autowired
    private MinistryService ministryService;
    @Autowired
    private DanceRepository danceRepository;
    @Autowired
    private MusicRespository musicRespository;
    @Autowired
    private MinistryRepository ministryRepository;

    @Autowired
    private RepertoryDanceMinistryRepository repertoryDanceMinistryRepository;

    @Autowired
    private RepertoryMusicMinistryRepository repertoryMusicMinistryRepository;

    private final RepertoryMapper mapper;
    private final MinistryMapper ministryMapper;

    public RepertoryService(RepertoryMapper repertoryMapper, MinistryMapper ministryMapper) {
        this.mapper = repertoryMapper;
        this.ministryMapper = ministryMapper;
    }

    @Override
    public RepertoryResponse createRepertory(RepertoryRequestDTO dto) {
        log.info("Creating repertorio in ministerio...");

        RepertoryMinistry repertorioModel = new RepertoryMinistry();

        Ministry ministry = ministryRepository.findById(dto.getMinistryId())
                .orElseThrow(() -> new MinistryIllegalArgumentException("Ministry not found"));


        repertorioModel.setMinistry(ministry);
        repertorioModel.setName(dto.getName());
        repertorioModel.setDescription(dto.getDescription());
        repertorioModel.setMinistryType(ministry.getMinistryType());
        repertorioModel.setLinkPlaylist(dto.getLinkPlaylist());

        RepertoryMinistry repertorio = this.repository.save(repertorioModel);
        switch (ministry.getMinistryType()) {
            case MUSIC:
                if (dto.getMusicMinistryIds() == null || dto.getMusicMinistryIds().isEmpty()) break;
                dto.getMusicMinistryIds().forEach(musicMinistryId -> {
                    repertoryMusicMinistryRepository.save(new RepertoryMusicMinistry(
                            new RepertoryMusicMinistryIds(repertorio.getId(), musicMinistryId)));
                });
                break;
            case DANCE:
                if (dto.getDanceMinistryIds() == null || dto.getDanceMinistryIds().isEmpty()) break;
                dto.getDanceMinistryIds().forEach(danceMinistryId -> {
                    repertoryDanceMinistryRepository.save(new RepertoryDanceMinistry(
                            new RepertoryDanceMinistryIds(repertorio.getId(), danceMinistryId)));
                });
                break;
        }
        log.info("Repertory " + repertorio.getId() + " created");
        return mapper.entityToResponse(repertorio);
    }

    @Override
    public List<RepertoryResponse> listRepertoryByMinistryId(UUID id) {
        log.info("Listing repertories ministry of ministry: " + id);
        return this.mapper.toListResponseRepertory(this.repository.findAllByMinistry_Id(id));
    }

    @Override
    public RepertoryResponse getRepertoryById(UUID id) {
        Optional<RepertoryMinistry> repertoryOptional = this.repository.findById(id);

        if (repertoryOptional.isEmpty()) {
            throw new RepertoryNotFoundException("Repertory Id: " + id + " not found!");
        }
        return mapper.entityToResponse(repertoryOptional.get());
    }

    @Override
    public RepertoryShortResponse getRepertoryShortById(UUID id) {
        Optional<RepertoryMinistry> repertoryOptional = this.repository.findById(id);

        if (repertoryOptional.isEmpty()) {
            throw new RepertoryNotFoundException("Repertory Id: " + id + " not found!");
        }
        RepertoryMinistry response = repertoryOptional.get();
        return new RepertoryShortResponse(
                response.getId(), response.getName(), response.getDescription(), response.getMinistry().getId()
        );
    }

    @Override
    public List<RepertoryResponse> listAllRepertory() {
        log.info("Listing all repertories...");
        List<RepertoryMinistry> repertoryList = this.repository.findAll();

        if (repertoryList.isEmpty()) {
            throw new ListRepertoryEmptyException("No repertories found! Maybe you need to create one!");
        }
        List<RepertoryResponse> response = new ArrayList<>();
        for (RepertoryMinistry item : repertoryList) {
            response.add(
                    mapper.entityToResponse(item)
                        );
        }
        return response;
    }

    @Override
    public List<RepertoryShortResponse> listAllRepertoryByMinistryId(UUID ministryId) {
        List<RepertoryMinistry> allByMinistryId = this.repository.findAllByMinistry_Id(ministryId);
        if (allByMinistryId.isEmpty()) {
            throw new ListRepertoryEmptyException("No repertories found! Maybe you need to create one!");
        }
        List<RepertoryShortResponse> response = new ArrayList<>();
        for (RepertoryMinistry item : allByMinistryId) {
            response.add(
                    new RepertoryShortResponse(item.getId(), item.getName(), item.getDescription(),
                            item.getMinistry().getId())
                        );
        }
        return response;
    }

    @Override
    public RepertoryResponse editRepertory(UUID id, RepertoryRequestDTO requestDTO) {
        log.info("Editing repertory ministry " + id + "...");
        RepertoryMinistry oldRepertory = mapper.responseToEntity(this.getRepertoryById(id));
        Ministry ministry = ministryMapper.responseToEntity(
                this.ministryService.getMinistryById(requestDTO.getMinistryId()));

        oldRepertory.setName(requestDTO.getName());
        oldRepertory.setDescription(requestDTO.getDescription());

        if (requestDTO.getDanceMinistryIds() != null && !(requestDTO.getDanceMinistryIds().isEmpty())) {
            log.info("EDITING DANCE MINISTRY OF REPERTORY " + id + "...");
            oldRepertory.getDanceMinistryList().forEach(repertoryDanceMinistry -> {
                Optional<RepertoryDanceMinistry> repertoryDanceMinistryOptional = repertoryDanceMinistryRepository.findById(
                        new RepertoryDanceMinistryIds(oldRepertory.getId(), repertoryDanceMinistry.getId()));
                repertoryDanceMinistryOptional.ifPresent(danceMinistry -> repertoryDanceMinistryRepository.delete(
                        danceMinistry));
            });
            requestDTO.getDanceMinistryIds().forEach(danceMinistryId -> {
                repertoryDanceMinistryRepository.save(new RepertoryDanceMinistry(
                        new RepertoryDanceMinistryIds(oldRepertory.getId(), danceMinistryId)));
            });
        }

        if (requestDTO.getMusicMinistryIds() != null && !(requestDTO.getMusicMinistryIds().isEmpty())) {
            log.info("EDITING MUSIC MINISTRY OF REPERTORY " + id + "...");

            oldRepertory.getMusicMinistryList().forEach(musicMinistry -> {
                System.out.println("DELETANDO MUSIC MINISTRY " + musicMinistry.getId() + "...");
                Optional<RepertoryMusicMinistry> repertoryMusicMinistryOptional = repertoryMusicMinistryRepository.findById(
                        new RepertoryMusicMinistryIds(oldRepertory.getId(), musicMinistry.getId()));
                repertoryMusicMinistryOptional.ifPresent(
                        repertoryMusicMinistry -> repertoryMusicMinistryRepository.delete(
                                repertoryMusicMinistry));
            });
            requestDTO.getMusicMinistryIds().forEach(musicMinistryId -> {
                System.out.println("CRIANDO MUSIC MINISTRY " + musicMinistryId + "...");
                repertoryMusicMinistryRepository.save(new RepertoryMusicMinistry(
                        new RepertoryMusicMinistryIds(oldRepertory.getId(), musicMinistryId)));
            });
        }
        oldRepertory.setMinistry(ministry);

        RepertoryMinistry repertoryEdited = this.repository.save(oldRepertory);
        log.info("Repertorio " + repertoryEdited.getId() + " edited");
        return mapper.entityToResponse(repertoryEdited);
    }

    @Override
    public void deleteRepertory(UUID id) {
        log.info("Deleting repertory ministry " + id + "...");
        RepertoryMinistry repertory = mapper.responseToEntity(getRepertoryById(id));
        this.repository.delete(repertory);
//        List<EventScale> scalesMinistry = this.scaleRepository.findByRepertoryIdsContaining(id);

//        if (scalesMinistry.isEmpty()) {
//            throw new RepertoryNotFoundException("Repertory Id: " + id + " not found! Maybe you need to create one!");
//        }

        log.info("Repertorio " + repertory.getId() + " deleted");
    }

    @Override
    public RepertoryResponse addOrRemoveMusicsRepertory(UUID idRepertory, MusicRepertoryAddRemoveDTO musicDTO) {
        log.info("Adding or removing musics repertory...");
        RepertoryMinistry repertoryMinistry = mapper.responseToEntity(this.getRepertoryById(idRepertory));
        if (musicDTO.getMusicIdsAdd() != null) {
            musicDTO.getMusicIdsAdd().forEach(musicId -> {
                repertoryMusicMinistryRepository.save(
                        new RepertoryMusicMinistry(new RepertoryMusicMinistryIds(repertoryMinistry.getId(), musicId)));
            });
        }
        if (musicDTO.getMusicIdsRemove() != null) {
            musicDTO.getMusicIdsRemove().forEach(id -> {
                repertoryMusicMinistryRepository.deleteById(
                        new RepertoryMusicMinistryIds(repertoryMinistry.getId(), id));
            });
        }

        RepertoryMinistry repertoryUpdated = this.repository.findById(repertoryMinistry.getId())
                .orElseThrow(() -> new RepertoryNotFoundException("Repertory not found!"));
        return mapper.entityToResponse(repertoryUpdated);
    }

    @Override
    public RepertoryResponse addOrRemoveDanceRepertory(UUID idRepertory, DanceRepertoryAddRemoveDTO danceDTO) {
        log.info("Adding or removing dances repertory...");
        RepertoryMinistry repertoryMinistry = mapper.responseToEntity(this.getRepertoryById(idRepertory));

        if (danceDTO.getDanceIdsAdd() != null) {
            danceDTO.getDanceIdsAdd().forEach(danceId -> {
                repertoryDanceMinistryRepository.save(
                        new RepertoryDanceMinistry(new RepertoryDanceMinistryIds(repertoryMinistry.getId(), danceId)));
            });
        }
        if (danceDTO.getDanceIdsRemove() != null) {
            danceDTO.getDanceIdsRemove().forEach(id -> {
                repertoryDanceMinistryRepository.deleteById(
                        new RepertoryDanceMinistryIds(repertoryMinistry.getId(), id));
            });
        }

        RepertoryMinistry repertoryUpdated = this.repository.findById(repertoryMinistry.getId())
                .orElseThrow(() -> new RepertoryNotFoundException("Repertory not found!"));
        return mapper.entityToResponse(repertoryUpdated);
    }


    @Override
    public RepertoryResponse listRepertoryWithDancesAndMusic(UUID idRepertorio) {
        log.info("Listing repertorio with dances and music");
        return mapper.entityToResponse(this.repository.findByIdWithDanceAndMusicInfos(idRepertorio).get());
    }

    @Override
    public List<MusicResponse> listMusicsByRepertoryId(UUID repertoryId) {
        List<MusicMinistry> response = this.repository.findAllMusicByRepertoryId(repertoryId);
        if (response.isEmpty()) {
            throw new ListRepertoryEmptyException(
                    "List is empty! Maybe you need to add one music in repertory by id " + repertoryId + " !");
        }
        return mapper.toListMusicResponse(response);
    }

    @Override
    public List<DanceResponse> listDancesByRepertoryId(UUID repertoryId) {
        List<DanceMinistry> response = this.repository.findAllDancesByRepertoryId(repertoryId);
        if (response.isEmpty()) {
            throw new ListRepertoryEmptyException(
                    "List is empty! Maybe you need to add one dance in repertory by id " + repertoryId + " !");
        }
        return mapper.toListDanceResponse(response);
    }

}
