package br.hallel.relational.api.app.ministry.interfaces;


import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MinistryInterface {
    MinistryResponse
    createMinistry(MinistryRequestDTO ministryRequestDTO, MultipartFile image);

    List<MinistryResponse> listAllMinistries(int page, int size);

    MinistryResponse getMinistryById(UUID id);

    MinistryResponse
    editMinistry(UUID id, MinistryRequestDTO ministryRequestDTO, MultipartFile image);

    void deleteMinistryById(UUID id);

    List<EventScaleResponse> listAllEventScales(UUID ministryId);
}
