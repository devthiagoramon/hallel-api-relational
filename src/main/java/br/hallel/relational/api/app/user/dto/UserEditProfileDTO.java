package br.hallel.relational.api.app.user.dto;

import java.util.Date;

public record UserEditProfileDTO(
        String name,
        String email,
        String phoneNumber,
        Date dateBirth,
        String cpf
) {
}
