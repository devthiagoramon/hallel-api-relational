package br.hallel.relational.api.app.event.dto.mapper;

import br.hallel.relational.api.app.event.dto.MemberEventScaleResponseUserInfos;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MemberEventScaleMapper {


    MemberEventScaleResponseUserInfos modelToResponseWithUserInfos(
            MemberEventScale memberEventScale);
}
