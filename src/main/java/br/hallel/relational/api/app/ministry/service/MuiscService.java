package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.MusicAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.MusicResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.dto.mapper.RepertoryMapper;
import br.hallel.relational.api.app.ministry.exception.RepertoryNotFoundException;
import br.hallel.relational.api.app.ministry.interfaces.MusicInterface;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import br.hallel.relational.api.app.ministry.repository.MusicRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MuiscService implements MusicInterface {

    @Autowired
    private MinistryService ministryService;
    @Autowired
    private MusicRespository respository;
    private final MinistryMapper mapper;
    private final RepertoryMapper repertoryMapper;

    public MuiscService(MinistryMapper ministryMapper, RepertoryMapper repertoryMapper) {
        this.mapper = ministryMapper;
        this.repertoryMapper = repertoryMapper;
    }

    @Override
    public MusicResponse createMuisc(MusicAddEditDTO musicDTO) {
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
    public MusicResponse getMuiscById(UUID id) {
        Optional<MusicMinistry> musicOptional = this.respository.findById(id);
        if (musicOptional.isPresent()) {
            throw new RepertoryNotFoundException("Music Id: " + id + " not found");
        }
        return repertoryMapper.musicEntityToResponse(musicOptional.get());
    }

    @Override
    public void deleteMuiscById(UUID id) {
        log.info("Deleting music... {}", id);
        MusicMinistry muiscById = repertoryMapper.musicResponseToEntity(this.getMuiscById(id));
        log.info("Music {} deleted!", id);
        this.respository.delete(muiscById);
    }

    @Override
    public List<MusicResponse> listAllMusics() {
        return this.repertoryMapper.toListMusicResponse(this.respository.findAll());
    }
}
