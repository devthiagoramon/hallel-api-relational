package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.MemberEventScaleResponseUserInfos;
import br.hallel.relational.api.app.event.dto.mapper.MemberEventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberEventScaleService {

    private final MemberEventScaleRepository memberEventScaleRepository;
    private final UserRepository userRepository;
    private final EventScaleRepository eventScaleRepository;
    private final MemberEventScaleMapper mapper;

    public MemberEventScaleResponseUserInfos inviteUserIntoScale(
            UUID eventScaleId, UUID userId) {
        log.info("Inviting user {} into scale {}", userId, eventScaleId);
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException("User with id %s not found".formatted(userId)));
        EventScale eventScale = eventScaleRepository.findById(eventScaleId)
                                                    .orElseThrow(() -> new EventScaleNotFoundException("Event scale with id %s not found".formatted(eventScaleId)));

        MemberEventScale memberEventScale = memberEventScaleRepository.save(new MemberEventScale(MemberEventScaleStatus.CONVIDADO, null, user, eventScale));

        return mapper.modelToResponseWithUserInfos(memberEventScale);
    }
}
