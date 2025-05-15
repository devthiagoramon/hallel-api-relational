package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryMemberResponse;
import br.hallel.relational.api.app.ministry.exception.FunctionMinistryNotFound;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import br.hallel.relational.api.app.ministry.model.FunctionMinistryMember;
import br.hallel.relational.api.app.ministry.model.FunctionMinistryMemberId;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryMemberRepository;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FunctionMinistryMemberService {

    private final FunctionMinistryMemberRepository functionMinistryMemberRepository;
    private final FunctionMinistryRepository functionMinistryRepository;
    private final UserRepository userRepository;

    public FunctionMinistryMemberResponse associateAFunctionMinistryToMember(
            UUID functionMinistryId, UUID userId) {
        log.info("Associate function {} to user {}", functionMinistryId, userId);
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException("Can't find user id %s".formatted(userId.toString())));
        FunctionMinistry functionMinistry = functionMinistryRepository.findById(functionMinistryId)
                                                                      .orElseThrow(() -> new FunctionMinistryNotFound("Can't find function ministry id %s".formatted(functionMinistryId.toString())));
        FunctionMinistryMember save = functionMinistryMemberRepository.save(new FunctionMinistryMember(new FunctionMinistryMemberId(userId, functionMinistryId), user, functionMinistry));

        return new FunctionMinistryMemberResponse(save.getId());
    }

    public void removeFunctionMinistryMember(UUID functionMinistryId,
                                             UUID userId) {
        log.info("Remove function {} from user {}", functionMinistryId, userId);
        functionMinistryMemberRepository.deleteById(new FunctionMinistryMemberId(userId, functionMinistryId));
    }

}


