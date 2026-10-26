package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;

@Mapper(componentModel = "spring")
public interface PassportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Passport toEntity(PassportRequest request);
    PassportResponse toResponse(Passport entity);
    List<PassportResponse> toResponseList(List<Passport> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(PassportRequest request, @MappingTarget Passport entity);

}
