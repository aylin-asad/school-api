package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    Author toEntity(AuthorRequest request);
    AuthorResponse toResponse(Author entity);
    List<AuthorResponse> toResponseList(List<Author> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    void updateEntity(AuthorRequest request, @MappingTarget Author entity);

}
