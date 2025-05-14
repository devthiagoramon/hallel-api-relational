package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;

import br.hallel.relational.api.app.ministry.dto.*;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.dto.mapper.RepertoryMapper;
import br.hallel.relational.api.app.ministry.exception.RepertoryNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.RepertoryInterface;
import br.hallel.relational.api.app.ministry.model.DanceMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import br.hallel.relational.api.app.ministry.repository.DanceRepository;
import br.hallel.relational.api.app.ministry.repository.MusicRespository;
import br.hallel.relational.api.app.ministry.repository.RepertoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final RepertoryMapper mapper;
    private final MinistryMapper ministryMapper;

    public RepertoryService(RepertoryMapper repertoryMapper, MinistryMapper ministryMapper) {
        this.mapper = repertoryMapper;
        this.ministryMapper = ministryMapper;
    }

    @Override
    public RepertoryResponse createRepertory(RepertoryRequestDTO requestDTO) {
        log.info("Creating repertorio in ministerio...");
        RepertoryMinistry repertorioModel = new RepertoryMinistry();

        Ministry ministry = ministryMapper.
                responseToEntity(this.ministryService.getMinistryById(requestDTO.getMinistry()));

        repertorioModel.setMinistry(ministry);
        repertorioModel.setName(requestDTO.getName());
        repertorioModel.setDescription(requestDTO.getDescription());
        repertorioModel.setPlaylistRepertoryList(repertorioModel.getPlaylistRepertoryList());
        repertorioModel.setVideoMinistryList(requestDTO.getVideoMinistryList());
        repertorioModel.setDanceMinistryList(requestDTO.getDanceMinistryList());
        repertorioModel.setMusicMinistryList(requestDTO.getMusicMinistryList());

        RepertoryMinistry repertorio = this.repository.save(repertorioModel);
        log.info("Repertory " + repertorio.getId() + " created");
        return mapper.entityToResponse(repertorio);
    }

    @Override
    public List<RepertoryResponse> listRepertoryByMinistryId(UUID id) {
        log.info("Listing repertories ministry of ministry: " + id);
        return this.mapper.toListResponseRepertory(
                this.repository.findAllByMinistry_Id(id)
        );
    }

    @Override
    public RepertoryResponse getRepertoryById(UUID id) {
        Optional<RepertoryMinistry> repertoryOptional = this.repository.findById(id);
        if (repertoryOptional.isPresent()) {
            throw new RepertoryNotFoundException("Repertory Id: " + id + " not found!");
        }

        return mapper.entityToResponse(repertoryOptional.get());
    }

    @Override
    public List<RepertoryResponse> listAllRepertory() {
        log.info("Listing all repertories...");
        List<RepertoryMinistry> repertoryList = this.repository.findAll();

        if (repertoryList.isEmpty()) {
            throw new RepertoryNotFoundException("No repertories found! Maybe you need to create one!");
        }

        return mapper.toListResponseRepertory(repertoryList);
    }

    @Override
    public RepertoryResponse editRepertory(UUID id, RepertoryRequestDTO requestDTO) {
        log.info("Editing repertory ministry " + id + "...");
        RepertoryMinistry oldRepertory =
                mapper.responseToEntity(this.getRepertoryById(id));
        Ministry ministry = ministryMapper.
                responseToEntity(this.ministryService.getMinistryById(requestDTO.getMinistry()));

        oldRepertory.setName(requestDTO.getName());
        oldRepertory.setDescription(requestDTO.getDescription());
        oldRepertory.setDanceMinistryList(requestDTO.getDanceMinistryList());
        oldRepertory.setMusicMinistryList(requestDTO.getMusicMinistryList());
        oldRepertory.setVideoMinistryList(requestDTO.getVideoMinistryList());
        oldRepertory.setPlaylistRepertoryList(requestDTO.getPlaylistRepertoryList());
        oldRepertory.setMinistry(ministry);

        RepertoryMinistry repertoryEdited = this.repository.save(oldRepertory);
        log.info("Repertorio " + repertoryEdited.getId() + " edited");
        return mapper.entityToResponse(repertoryEdited);
    }

    @Override
    public void deleteRepertory(UUID id) {
        log.info("Deleting repertory ministry " + id + "...");
        RepertoryMinistry repertory =
                mapper.responseToEntity(getRepertoryById(id));
        this.repository.delete(repertory);
        List<EventScale> scalesMinistry = this.scaleRepository.findByRepertoryIdsContaining(id);

        if (scalesMinistry.isEmpty()) {
            throw new RepertoryNotFoundException("Repertory Id: " + id + " not found! Maybe you need to create one!");
        }

        for (EventScale scale : scalesMinistry) {
            List<UUID> idsScales = scale.getRepertoryIds();
            idsScales.remove(id);
            scale.setRepertoryIds(idsScales);
            this.scaleRepository.save(scale);
        }

        log.info("Repertorio " + repertory.getId() + " deleted");
    }

    @Override
    public RepertoryResponse addOrRemoveMusicsRepertory(UUID idRepertory, MusicRepertoryAddRemoveDTO repertoryMusicDTO) {
        log.info("Adding or removing musics repertory...");
        RepertoryMinistry repertoryMinistry =
                mapper.responseToEntity(this.getRepertoryById(idRepertory));
        List<UUID> dancesIdList = new ArrayList<>();

        if (repertoryMinistry.getMusicMinistryList() != null) {
            List<UUID> dancesIdsMinistry =
                    repertoryMinistry.getDanceMinistryList().stream()
                            .map(DanceMinistry::getId)
                            .toList();
            dancesIdList = dancesIdsMinistry;
        }
        if (repertoryMusicDTO.getMusicIdsAdd() != null) {
            dancesIdList.addAll(repertoryMusicDTO.getMusicIdsAdd());
        }
        if (repertoryMusicDTO.getMusicIdsRemove() != null) {
            dancesIdList = dancesIdList.stream()
                    .filter(dance -> !(repertoryMusicDTO.getMusicIdsRemove()
                            .contains(dance)))
                    .toList();
        }

        List<DanceMinistry> danceList = this.danceRepository.findAllById(dancesIdList);

        repertoryMinistry.setDanceMinistryList(danceList);
        RepertoryMinistry repertoryUpdated = this.repository.save(repertoryMinistry);
        return mapper.entityToResponse(repertoryUpdated);
    }

    @Override
    public RepertoryResponse addOrRemoveDanceRepertory(UUID idRepertory, DanceRepertoryAddRemoveDTO repertoryDanceDTO) {
        log.info("Adding or removing musics repertory...");
        RepertoryMinistry repertoryMinistry =
                mapper.responseToEntity(this.getRepertoryById(idRepertory));
        List<UUID> musicsIdList = new ArrayList<>();

        if (repertoryMinistry.getMusicMinistryList() != null) {
            List<UUID> musicsIdsMinistry =
                    repertoryMinistry.getMusicMinistryList().stream()
                            .map(MusicMinistry::getId)
                            .toList();
            musicsIdList = musicsIdsMinistry;
        }
        if (repertoryDanceDTO.getDanceIdsAdd() != null) {
            musicsIdList.addAll(repertoryDanceDTO.getDanceIdsAdd());
        }
        if (repertoryDanceDTO.getDanceIdsRemove() != null) {
            musicsIdList = musicsIdList.stream()
                    .filter(music -> !(repertoryDanceDTO.getDanceIdsRemove()
                            .contains(music)))
                    .toList();
        }

        List<MusicMinistry> musicList = this.musicRespository.findAllById(musicsIdList);

        repertoryMinistry.setMusicMinistryList(musicList);
        RepertoryMinistry repertoryUpdated = this.repository.save(repertoryMinistry);
        return mapper.entityToResponse(repertoryUpdated);
    }

    @Override
    public RepertoryResponse listRepertoryWithDancesAndMusic(UUID idRepertorio) {
        log.info("Listing repertorio with dances and music");
        return
                mapper.entityToResponse(
                        this.repository.findByIdWithDanceAndMusicInfos(idRepertorio).get());
    }

    @Override
    public List<MusicReponse> listMusicsByRepertoryId(UUID repertoryId) {
        return mapper.toListMusicResponse(this.repository.findAllMusicByRepertoryId(repertoryId));
    }

    @Override
    public List<DanceResponse> listDancesByRepertoryId(UUID repertoryId) {
        return mapper.toListDanceResponse(this.repository.findAllDancesByRepertoryId(repertoryId));
    }

    @Override
    public MusicReponse editMusicRepertory(UUID id, MusicEditDTO musicEditDTO) {
        log.info("Editing music repertory...");

        RepertoryMinistry repertoryMinistry =
                mapper.responseToEntity(this.getRepertoryById(id));

        if (repertoryMinistry.getMusicMinistryList().isEmpty()) {
            throw new RepertoryNotFoundException("Music List is empty! Maybe you need to create one!");
        }

        MusicMinistry musicOld = repertoryMinistry.getMusicMinistryList().stream()
                .filter(m -> m.getId().equals(musicEditDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new RepertoryNotFoundException("Music not found in repertory."));

        musicOld.setName(musicEditDTO.getName());
        musicOld.setDescription(musicEditDTO.getDescription());
        musicOld.setLetter(musicEditDTO.getLetter());
        musicOld.setLink(musicEditDTO.getLink());

        if (musicEditDTO.getMinistry() != null) {
            Ministry ministry = this.ministryMapper
                    .responseToEntity(this.ministryService.getMinistryById(musicEditDTO.getMinistry()));
            musicOld.setMinistry(ministry);
        }

        for (int i = 0; i < repertoryMinistry.getMusicMinistryList().size(); i++) {
            if (repertoryMinistry.getMusicMinistryList().get(i).getId()
                    .equals(musicEditDTO.getId())) {
                repertoryMinistry.getMusicMinistryList().set(i, musicOld);
                log.info("Updated successfully... by id {}", musicEditDTO.getId());
                break;
            }
        }
        log.info("Saving Repertory...");
        this.repository.save(repertoryMinistry);
        return this.mapper.musicEntityToResponse(musicOld);
    }

    @Override
    public DanceResponse editDanceRepertory(UUID id, DanceEditDTO danceEditDTO) {
        log.info("Editing music repertory...");

        RepertoryMinistry repertoryMinistry =
                mapper.responseToEntity(this.getRepertoryById(id));

        if (repertoryMinistry.getDanceMinistryList().isEmpty()) {
            throw new RepertoryNotFoundException("Dance List is emptyId! Maybe you need to create one!");
        }

        DanceMinistry danceOld = repertoryMinistry.getDanceMinistryList().stream()
                .filter(d -> d.getId().equals(danceEditDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new RepertoryNotFoundException("Music not found in repertory."));

        danceOld.setName(danceEditDTO.getName());
        danceOld.setDescription(danceEditDTO.getDescription());
        danceOld.setLink(danceEditDTO.getLink());

        if (danceEditDTO.getMinistry() != null) {
            Ministry ministry = this.ministryMapper
                    .responseToEntity(this.ministryService.getMinistryById(danceEditDTO.getMinistry()));
            danceOld.setMinistry(ministry);
        }

        for (int i = 0; i < repertoryMinistry.getDanceMinistryList().size(); i++) {
            if (repertoryMinistry.getDanceMinistryList().get(i).getId()
                    .equals(danceEditDTO.getId())) {
                repertoryMinistry.getDanceMinistryList().set(i, danceOld);
                log.info("Updated successfully... by id {}", danceEditDTO.getId());
                break;
            }
        }
        log.info("Saving Repertory...");
        this.repository.save(repertoryMinistry);
        return this.mapper.danceEntityToResponse(danceOld);
    }
}
