package br.hallel.relational.api.app.user.dto.mapper;

import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @Mapping(target = "date_view", ignore = true)
    UserProfileResponse userEditProfileToResponse(User user);
}
