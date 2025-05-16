package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.NotConfirmedDTO;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.NotConfirmedScaleMinistry;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.NotConfirmedScaleMinistryRepository;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotConfirmedScaleService {

    @Autowired
    private NotConfirmedScaleMinistryRepository repository;
    @Autowired
    private EventScaleRepository eventScaleRepository;
    @Autowired
    private MemberMinistryRepository memberRepository;
    private final EventScaleMapper eventScaleMapper;

    public NotConfirmedScaleService(EventScaleMapper eventScaleMapper) {
        this.eventScaleMapper = eventScaleMapper;

    }

    public NotConfirmedScaleMinistry createNotConfirmedScaleMinistry(
            NotConfirmedDTO dto) {
        log.info("Creating nao confirmado escala... ");

        EventScale scale =
                this.eventScaleRepository.findById(dto.getEventScale()).get();
        MemberMinistry member = memberRepository.findById(dto.getMember()).get();

        NotConfirmedScaleMinistry notConfirmed =
                new NotConfirmedScaleMinistry(member, scale, dto.getReason());
        return repository
                .save(notConfirmed);
    }

    public List<NotConfirmedScaleMinistry> listAllNotConfirmedScaleMinistry() {
        return repository.findAll();
    }

    public NotConfirmedScaleMinistry update(UUID notConfirmedId, NotConfirmedDTO dto) {
        log.info("Updating not confirmed scale entry...");
        NotConfirmedScaleMinistry existing = getById(notConfirmedId);
        existing.setReason(dto.getReason());
        return repository.save(existing);
    }

    public NotConfirmedScaleMinistry getById(UUID id) {
        log.info("Fetching not confirmed scale entry by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not confirmed scale entry not found"));
    }

    public List<NotConfirmedScaleMinistry> getByUserId(MemberMinistryId memberMinistryId) {
        log.info("Fetching all not confirmed scale entries by userId: {}", memberMinistryId.getUserId());
        return repository.findAllByMemberMinistry_Id(memberMinistryId);
    }

    public void delete(UUID id) {
        log.info("Deleting not confirmed scale entry: {}", id);
        NotConfirmedScaleMinistry existing = getById(id);
        repository.delete(existing);
        log.info("Deleted not confirmed scale entry: {}", id);
    }

    public boolean existsByUserAndScale(UUID scaleId, MemberMinistryId memberMinistryId) {
        log.info("Verifying if user {} has already not confirmed scale {}", memberMinistryId, scaleId);
        return repository.findByMemberMinistry_IdAndEventScale_Id(memberMinistryId, scaleId).isPresent();
    }
}
