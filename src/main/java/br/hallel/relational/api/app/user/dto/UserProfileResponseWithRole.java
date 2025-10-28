package br.hallel.relational.api.app.user.dto;

import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.model.UserAccountStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record UserProfileResponseWithRole(UUID id,
                                          String name,
                                          String email,
                                          String phoneNumber,
                                          Date dateBirth,
                                          String fileImageUrl,
                                          String cpf,
                                          UserAccountStatus status,
                                          Date date_view,
                                          Date last_access,
                                          List<String> roles) {


}
