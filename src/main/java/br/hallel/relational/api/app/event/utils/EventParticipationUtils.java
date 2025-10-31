package br.hallel.relational.api.app.event.utils;

import br.hallel.relational.api.app.event.dto.ValidateAgeParticipantResponse;
import br.hallel.relational.api.app.event.exception.UserValidationException;
import br.hallel.relational.api.app.event.model.enum_type.AgeGroup;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.LimitEventAgeGroup;
import br.hallel.relational.api.app.event.repository.LimitEventAgeGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventParticipationUtils {

    private final LimitEventAgeGroupRepository limitEventAgeGroupRepository;

    public ValidateAgeParticipantResponse validateAgeParticipant(int years, Event event) {
        AgeGroup targetAgeGroup;

        targetAgeGroup = getAgeGroup(years);

        LimitEventAgeGroup limit = limitEventAgeGroupRepository
                .findByEventIdAndAgeGroup(event.getId(), targetAgeGroup)
                .orElseThrow(() -> new RuntimeException(
                        "Limite de vagas não configurado para faixa etária: " + targetAgeGroup));

        if (limit.getLimitQuantity() <= limit.getCurrentQuantity()) {
            log.warn("Limite atingido para {}", targetAgeGroup);
            return new ValidateAgeParticipantResponse(targetAgeGroup, AgeGroup.EXCEDIDO);
        }

        limit.setCurrentQuantity(limit.getCurrentQuantity() + 1);
        limitEventAgeGroupRepository.save(limit);

        return new ValidateAgeParticipantResponse(targetAgeGroup, null);
    }



    public static AgeGroup getAgeGroup(int years) {
        AgeGroup targetAgeGroup;
        if (years <= 8) targetAgeGroup = AgeGroup.CRIANCA;
        else if (years <= 14) targetAgeGroup = AgeGroup.TEEN;
        else if (years <= 30) targetAgeGroup = AgeGroup.JOVEM;
        else if (years <= 120) targetAgeGroup = AgeGroup.ADULTO;
        else throw new UserValidationException("A idade do usuário é inválida.");
        return targetAgeGroup;
    }

    public int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }


}
