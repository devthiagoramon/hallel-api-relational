package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryDTO;
import br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.FunctionMinistryMapper;
import br.hallel.relational.api.app.ministry.exception.FunctionMinistryNotFound;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionMinistryService {

    private final FunctionMinistryRepository functionMinistryRepository;
    private final FunctionMinistryMapper mapper;

    public FunctionMinistryResponse addNewFunctionIntoMinistry(UUID ministryId, FunctionMinistryDTO dto) {
        log.info("Add new function into ministry {}", ministryId);
        FunctionMinistry functionMinistry = mapper.dtoToModel(dto);
        functionMinistry.setMinistryId(ministryId);
        return mapper.modelToResponse(functionMinistryRepository.save(functionMinistry));
    }

    public List<FunctionMinistryResponse> getAllFunctionsInMinistry(UUID ministryId) {
        log.info("Get all functions in ministry {}", ministryId);
        return mapper.listModelToResponseModel(functionMinistryRepository.listAllFunctionsByMinistryId(ministryId));
    }

    public FunctionMinistry getMinistryById(UUID functionMinistryId) {
        log.info("Get ministry {}", functionMinistryId);
        return functionMinistryRepository.listById(functionMinistryId).orElseThrow(() -> new FunctionMinistryNotFound("Function ministry not found by id %s".formatted(functionMinistryId.toString())));
    }

    public FunctionMinistryResponse editFunctionMinistryById(UUID functionMinistryId, FunctionMinistryDTO dto) {
        log.info("Edit function ministry {}", functionMinistryId);
        FunctionMinistry oldFunctionMinistry = getMinistryById(functionMinistryId);
        oldFunctionMinistry.setName(dto.getName());
        oldFunctionMinistry.setDescription(dto.getDescription());
        oldFunctionMinistry.setColor(dto.getColor());
        oldFunctionMinistry.setIcon(dto.getIcon());
        return mapper.modelToResponse(functionMinistryRepository.save(oldFunctionMinistry));
    }

    public void deleteFunctionMinistryById(UUID functionMinistryId) {
        log.info("Delete function ministry {}", functionMinistryId);
        FunctionMinistry functionMinistry = getMinistryById(functionMinistryId);
        functionMinistryRepository.delete(functionMinistry);
    }
}
