package br.hallel.relational.api.app.user.dto;

import br.hallel.relational.api.app.user.model.UserAccountStatus;

import java.util.Date;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        Date dateBirth,
        String fileImageUrl,
        String cpf,
        UserAccountStatus status,
        Date date_view,
        Date last_access
) {

}
