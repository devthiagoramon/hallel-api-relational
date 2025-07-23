package br.hallel.relational.api.app.user.dto;

import br.hallel.relational.api.app.security.dto.TokenDTO;

public record UserProfileResponseWithToken(
        TokenDTO token ,
        UserProfileResponse profile
) {
}
