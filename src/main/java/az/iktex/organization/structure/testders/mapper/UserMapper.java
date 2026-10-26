package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.UserResponse;

@Mapper(componentModel = "spring", uses = PassportMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passport", ignore = true)
    User toEntity(UserRequest request);
    UserResponse toResponse(User entity);
    List<UserResponse> toResponseList(List<User> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passport", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget User entity);

}
