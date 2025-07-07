package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryMemberResponse;
import br.hallel.relational.api.app.ministry.exception.FunctionMinistryNotFound;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import br.hallel.relational.api.app.ministry.model.FunctionMinistryMember;
import br.hallel.relational.api.app.ministry.model.FunctionMinistryMemberId;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryMemberRepository;
import br.hallel.relational.api.app.ministry.repository.FunctionMinistryRepository;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
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
    private final MemberMinistryRepository memberMinistryRepository;

    public FunctionMinistryMemberResponse associateAFunctionMinistryToMember(
            UUID functionMinistryId, UUID memberMinistryId) {
        log.info("Associate function {} to user {}", functionMinistryId, memberMinistryId);
        MemberMinistry memberMinistry = memberMinistryRepository.findById(memberMinistryId)
                                  .orElseThrow(() -> new MemberMinistryRegisterNotFoundException("Member ministry not found by id %s".formatted(memberMinistryId)));
        FunctionMinistry functionMinistry = functionMinistryRepository.findById(functionMinistryId)
                                                                      .orElseThrow(() -> new FunctionMinistryNotFound("Can't find function ministry id %s".formatted(functionMinistryId.toString())));
        FunctionMinistryMember save = functionMinistryMemberRepository.save(new FunctionMinistryMember(new FunctionMinistryMemberId(memberMinistryId, functionMinistryId), memberMinistry, functionMinistry));

        return new FunctionMinistryMemberResponse(save.getId());
    }

    public void removeFunctionMinistryMember(UUID functionMinistryId,
                                             UUID memberMinistryId) {
        log.info("Remove function {} from user {}", functionMinistryId, memberMinistryId);
        functionMinistryMemberRepository.deleteById(new FunctionMinistryMemberId(memberMinistryId, functionMinistryId));
    }

}


