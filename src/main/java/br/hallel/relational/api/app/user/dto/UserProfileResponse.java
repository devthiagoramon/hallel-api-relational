package br.hallel.relational.api.app.user.dto;

import java.util.Date;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        Date dateBirth,
        String fileImageUrl,
        String cpf
) {

}
