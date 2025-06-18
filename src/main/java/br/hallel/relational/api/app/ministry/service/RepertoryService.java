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
    private PlaylistRepository playlistRepertoryRepository;

    @Autowired
    private MinistryRepository ministryRepository;

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
        if (dto.getMusicMinistryIds() != null) {
            List<MusicMinistry> musics = this.musicRespository.findAllById(dto.getMusicMinistryIds());
            repertorioModel.setMusicMinistryList(musics);
        }
        if (dto.getDanceMinistryIds() != null) {
            List<DanceMinistry> dances = this.danceRepository.findAllById(dto.getDanceMinistryIds());
            repertorioModel.setDanceMinistryList(dances);
        }

        RepertoryMinistry repertorio = this.repository.save(repertorioModel);
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
        Ministry ministry = ministryMapper.responseToEntity(this.ministryService.getMinistryById(requestDTO.getMinistryId()));

        oldRepertory.setName(requestDTO.getName());
        oldRepertory.setDescription(requestDTO.getDescription());
//        oldRepertory.setDanceMinistryList(requestDTO.getDanceMinistryList());
//        oldRepertory.setMusicMinistryList(requestDTO.getMusicMinistryList());
//        oldRepertory.setVideoMinistryList(requestDTO.getVideoMinistryList());
//        oldRepertory.setPlaylistRepertoryList(requestDTO.getPlaylistRepertoryList());
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
        List<UUID> musicIdList = new ArrayList<>();

        if (repertoryMinistry.getMusicMinistryList() != null) {
            List<UUID> musicIdsMinistry = new ArrayList<>(repertoryMinistry.getMusicMinistryList().stream().map(MusicMinistry::getId).toList());
            musicIdList = musicIdsMinistry;
        }
        if (musicDTO.getMusicIdsAdd() != null) {
            musicIdList.addAll(musicDTO.getMusicIdsAdd());
        }
        if (musicDTO.getMusicIdsRemove() != null) {
            musicIdList = musicIdList.stream().filter(dance -> !(musicDTO.getMusicIdsRemove().contains(dance))).toList();
        }

        List<MusicMinistry> musicList = this.musicRespository.findAllById(musicIdList);

        repertoryMinistry.setMusicMinistryList(musicList);
        RepertoryMinistry repertoryUpdated = this.repository.save(repertoryMinistry);
        return mapper.entityToResponse(repertoryUpdated);
    }

    @Override
    public RepertoryResponse addOrRemoveDanceRepertory(UUID idRepertory, DanceRepertoryAddRemoveDTO danceDTO) {
        log.info("Adding or removing dances repertory...");
        RepertoryMinistry repertoryMinistry = mapper.responseToEntity(this.getRepertoryById(idRepertory));
        List<UUID> danceIdList = new ArrayList<>();

        if (repertoryMinistry.getDanceMinistryList() != null) {
            List<UUID> danceIdsMinistry = new ArrayList<>(repertoryMinistry.getDanceMinistryList().stream().map(DanceMinistry::getId).toList());
            danceIdList = danceIdsMinistry;
        }
        if (danceDTO.getDanceIdsAdd() != null) {
            danceIdList.addAll(danceDTO.getDanceIdsAdd());
        }
        if (danceDTO.getDanceIdsRemove() != null) {
            danceIdList = danceIdList.stream().filter(music -> !(danceDTO.getDanceIdsRemove().contains(music))).toList();
        }

        List<DanceMinistry> danceList = this.danceRepository.findAllById(danceIdList);

        repertoryMinistry.setDanceMinistryList(danceList);
        RepertoryMinistry repertoryUpdated = this.repository.save(repertoryMinistry);
        return mapper.entityToResponse(repertoryUpdated);
    }

    @Override
    public RepertoryResponse addOrRemovePlaylistRepertory(UUID idRepertory, PlaylistAddRemoveDTO playlistDTO) {
        log.info("Adding or removing playlists from repertory...");

        RepertoryMinistry repertory = mapper.responseToEntity(this.getRepertoryById(idRepertory));

        PlaylistRepertory playlist = repertory.getPlaylistRepertoryList().stream().filter(pl -> pl.getMinistryType() == playlistDTO.getMinistryType()).findFirst().orElseGet(() -> {
            PlaylistRepertory newPlaylist = new PlaylistRepertory();
            newPlaylist.setMinistryType(playlistDTO.getMinistryType());
            return newPlaylist;
        });

        if (playlistDTO.getAddMusicMinistry() != null) {
            List<MusicMinistry> musicsToAdd = musicRespository.findAllById(playlistDTO.getAddMusicMinistry());
            if (playlist.getMusicMinistries() == null) {
                playlist.setMusicMinistries(new ArrayList<>());
            }
            Set<UUID> existingIds = playlist.getMusicMinistries().stream().map(MusicMinistry::getId).collect(Collectors.toSet());
            for (MusicMinistry music : musicsToAdd) {
                if (!existingIds.contains(music.getId())) {
                    playlist.getMusicMinistries().add(music);
                }
            }
        }

        if (playlistDTO.getRemoveMusicMinistry() != null && playlist.getMusicMinistries() != null) {
            playlist.getMusicMinistries().removeIf(music -> playlistDTO.getRemoveMusicMinistry().contains(music.getId()));
        }

        if (playlistDTO.getAddDanceMinistry() != null) {
            List<DanceMinistry> dancesToAdd = danceRepository.findAllById(playlistDTO.getAddDanceMinistry());
            if (playlist.getDanceMinistries() == null) {
                playlist.setDanceMinistries(new ArrayList<>());
            }
            Set<UUID> existingIds = playlist.getDanceMinistries().
                    stream().map(DanceMinistry::getId).collect(Collectors.toSet());
            for (DanceMinistry dance : dancesToAdd) {
                if (!existingIds.contains(dance.getId())) {
                    playlist.getDanceMinistries().add(dance);
                }
            }
        }

        if (playlistDTO.getRemoveDanceMinistry() != null && playlist.getDanceMinistries() != null) {
            playlist.getDanceMinistries().removeIf(dance -> playlistDTO.getRemoveDanceMinistry().contains(dance.getId()));
        }

        if (!repertory.getPlaylistRepertoryList().contains(playlist)) {
            repertory.getPlaylistRepertoryList().add(playlist);
        }

        PlaylistRepertory playlistRepertory = this.playlistRepertoryRepository.save(playlist);

        RepertoryMinistry repertoryUpdated = this.repository.save(repertory);
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
            throw new ListRepertoryEmptyException("List is empty! Maybe you need to add one music in repertory by id " + repertoryId + " !");
        }
        return mapper.toListMusicResponse(response);
    }

    @Override
    public List<DanceResponse> listDancesByRepertoryId(UUID repertoryId) {
        List<DanceMinistry> response = this.repository.findAllDancesByRepertoryId(repertoryId);
        if (response.isEmpty()) {
            throw new ListRepertoryEmptyException("List is empty! Maybe you need to add one dance in repertory by id " + repertoryId + " !");
        }
        return mapper.toListDanceResponse(response);
    }

    @Override
    public List<PlaylistResponse> listPlaylistsByRepertoryId(UUID repertoryId) {
        List<PlaylistRepertory> response = this.repository.findAllPlaylistsByRepertoryId(repertoryId);
        if (response.isEmpty()) {
            throw new ListRepertoryEmptyException("List is empty! Maybe you need to add one dance in repertory by id " + repertoryId + " !");
        }
        return mapper.toListPlaylistResponse(response);
    }

    @Override
    public RepertoryMusicAndDanceResponse listMusicAndDanceByRepertoryId(UUID repertoryId) {
        RepertoryMusicAndDanceResponse response = new RepertoryMusicAndDanceResponse();

        response.setMusicMinistryList(
                mapper.toListMusicResponse(this.repository.findAllMusicByRepertoryId(repertoryId))
        );
        response.setDanceMinistryList(
                mapper.toListDanceResponse(this.repository.findAllDancesByRepertoryId(repertoryId))
        );
        response.setId(repertoryId);
        return response;
    }

    @Override
    public MusicResponse editMusicRepertory(UUID idRepertory, UUID idMusic, MusicAddEditDTO musicAddEditDTO) {
        log.info("Editing music repertory...");

        RepertoryMinistry repertoryMinistry = mapper.responseToEntity(this.getRepertoryById(idRepertory));

        if (repertoryMinistry.getMusicMinistryList().isEmpty()) {
            throw new ListRepertoryEmptyException("Music List is empty! Maybe you need to create one!");
        }

        MusicMinistry musicOld = repertoryMinistry.getMusicMinistryList().stream().filter(m -> m.getId().equals(idMusic)).findFirst().orElseThrow(() -> new RepertoryNotFoundException("Music not found in repertory."));

        musicOld.setName(musicAddEditDTO.getName());
        musicOld.setDescription(musicAddEditDTO.getDescription());
        musicOld.setLetter(musicAddEditDTO.getLetter());
        musicOld.setLink(musicAddEditDTO.getLink());

        if (musicAddEditDTO.getMinistry() != null) {
            Ministry ministry = this.ministryMapper.responseToEntity(this.ministryService.getMinistryById(musicAddEditDTO.getMinistry()));
            musicOld.setMinistry(ministry);
        }

        log.info("Saving Repertory...");
        this.repository.save(repertoryMinistry);
        return this.mapper.musicEntityToResponse(musicOld);
    }

    @Override
    public DanceResponse editDanceRepertory(UUID id, UUID idDance, DanceAddEditDTO danceEditDTO) {
        log.info("Editing music repertory...");

        RepertoryMinistry repertoryMinistry = mapper.responseToEntity(this.getRepertoryById(id));

        if (repertoryMinistry.getDanceMinistryList().isEmpty()) {
            throw new ListRepertoryEmptyException("Dance List is empty! Maybe you need to create one!");
        }

        DanceMinistry danceOld = repertoryMinistry.getDanceMinistryList().stream().filter(d -> d.getId().equals(idDance)).findFirst().orElseThrow(() -> new RepertoryNotFoundException("Music not found in repertory."));

        danceOld.setName(danceEditDTO.getName());
        danceOld.setDescription(danceEditDTO.getDescription());
        danceOld.setLink(danceEditDTO.getLink());
        if (danceEditDTO.getMinistry() != null) {
            Ministry ministry = this.ministryMapper.responseToEntity(this.ministryService.getMinistryById(danceEditDTO.getMinistry()));
            danceOld.setMinistry(ministry);
        }

        log.info("Saving Repertory...");
        this.repository.save(repertoryMinistry);
        return this.mapper.danceEntityToResponse(danceOld);
    }
}
