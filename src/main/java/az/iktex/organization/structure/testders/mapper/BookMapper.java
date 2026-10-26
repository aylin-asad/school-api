package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.response.AuthorResponse;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    Book toEntity(BookRequest request);
    @Named("summary")
    @Mapping(target = "authors", ignore = true)
    BookResponse toResponse(Book entity);
    @Named("withAuthors")
    @Mapping(target = "authors", source = "authorBooks")
    BookResponse toDetailResponse(Book entity);
    @IterableMapping(qualifiedByName = "summary")
    List<BookResponse> toResponseList(List<Book> entities);
    // Map the third entity to an author's DTO, without leaking a JPA entity.
    @Mapping(target = "id", source = "author.id")
    @Mapping(target = "name", source = "author.name")
    @Mapping(target = "surname", source = "author.surname")
    @Mapping(target = "country", source = "author.country")
    AuthorResponse authorBookToAuthorResponse(AuthorBook link);
    List<AuthorResponse> authorBookListToAuthorResponseList(List<AuthorBook> links);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    void updateEntity(BookRequest request, @MappingTarget Book entity);

}
