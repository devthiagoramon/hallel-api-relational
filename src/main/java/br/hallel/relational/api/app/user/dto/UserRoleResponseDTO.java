package br.hallel.relational.api.app.user.dto;

import java.util.List;
import java.util.UUID;

public record UserRoleResponseDTO(
        UUID id,
        List<String> userRoleName
) {
}
