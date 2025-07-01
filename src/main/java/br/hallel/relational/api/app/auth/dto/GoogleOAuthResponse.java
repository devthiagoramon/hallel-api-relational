package br.hallel.relational.api.app.auth.dto;

import br.hallel.relational.api.app.user.model.User;

public record GoogleOAuthResponse(String token, User user) {
}
