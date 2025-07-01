package br.hallel.relational.api.app.user.dto;

import java.util.UUID;

public record UserProfilePreferencesResponse(UUID id, boolean pushNotification){}
