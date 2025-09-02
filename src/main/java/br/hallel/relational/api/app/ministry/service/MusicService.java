package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.MusicAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.MusicResponse;
import br.hallel.relational.api.app.ministry.dto.MusicWithoutMinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.dto.mapper.RepertoryMapper;
import br.hallel.relational.api.app.ministry.exception.RepertoryNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.MusicInterface;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import br.hallel.relational.api.app.ministry.repository.MusicRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MusicService implements MusicInterface {

    @Autowired
    private MinistryService ministryService;
    @Autowired
    private MusicRespository respository;
    private final MinistryMapper mapper;
    private final RepertoryMapper repertoryMapper;

    public MusicService(MinistryMapper ministryMapper, RepertoryMapper repertoryMapper) {
        this.mapper = ministryMapper;
        this.repertoryMapper = repertoryMapper;
    }

    @Override
    public MusicResponse createMusic(MusicAddEditDTO musicDTO) {
        MusicMinistry musicModel = new MusicMinistry();

        Ministry ministry = mapper.responseToEntity(this.ministryService.getMinistryById(
                musicDTO.getMinistry()
        ));

        musicModel.setName(musicDTO.getName());
        musicModel.setDescription(musicDTO.getDescription());
        musicModel.setLink(musicDTO.getLink());
        musicModel.setLetter(musicDTO.getLetter());
        musicModel.setMinistry(ministry);
        MusicMinistry music = this.respository.save(musicModel);
        return repertoryMapper.musicEntityToResponse(music);
    }

    @Override
    public MusicResponse getMusicById(UUID id) {
        Optional<MusicMinistry> musicOptional = this.respository.findById(id);
        if (musicOptional.isEmpty()) {
            throw new RepertoryNotFoundException("music.not.found", id.toString());
        }
        return repertoryMapper.musicEntityToResponse(musicOptional.get());
    }

    @Override
    public void deleteMusicById(UUID id) {
        log.info("Deleting music... {}", id);
        MusicMinistry muiscById = repertoryMapper.musicResponseToEntity(this.getMusicById(id));
        log.info("Music {} deleted!", id);
        this.respository.delete(muiscById);
    }

    @Override
    public List<MusicResponse> listAllMusics() {
        return this.repertoryMapper.toListMusicResponse(this.respository.findAll());
    }
    public List<MusicResponse> listAllMusicsByMinistryId(UUID ministryId) {
        log.info("Listing all musics by ministryId {}", ministryId);
        return this.repertoryMapper.toListMusicResponse(this.respository.findAllByMinistry_Id(ministryId));
    }

    public MusicResponse editMusic(UUID musicMinistryId, MusicAddEditDTO musicDTO) {
        log.info("Editing music ... {}", musicMinistryId);
        Optional<MusicMinistry> musicOptional = this.respository.findById(musicMinistryId);
        if (musicOptional.isEmpty()) {
            throw new RepertoryNotFoundException("music.not.found", musicMinistryId.toString());
        }

        MusicMinistry music = musicOptional.get();
        music.setName(musicDTO.getName());
        music.setDescription(musicDTO.getDescription());
        music.setLink(musicDTO.getLink());
        music.setLetter(musicDTO.getLetter());

        return repertoryMapper.musicEntityToResponse(this.respository.save(music));
    }

    public Page<MusicWithoutMinistryResponse> listMusicMinistryByMinistryId(UUID ministryId, Pageable pageable) {
        log.info("Listing music ministry of ministry id {}", ministryId);
        return this.respository.listByMinistryId(ministryId, pageable);
    }
}
