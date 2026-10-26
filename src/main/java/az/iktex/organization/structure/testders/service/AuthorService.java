package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authors;
    private final BookRepository books;
    private final AuthorMapper mapper;
    private final BookMapper bookMapper;

    @Transactional public AuthorResponse create(AuthorRequest request) {
        checkDuplicate(request, null);
        return mapper.toResponse(authors.save(mapper.toEntity(request)));
    }
    public AuthorResponse get(Long id) { return mapper.toResponse(find(id)); }
    public List<AuthorResponse> all(String country) {
        return mapper.toResponseList(country == null ? authors.findAll() : authors.findAllByCountryIgnoreCase(country));
    }
    public List<AuthorResponse> withoutBooks() {
        return mapper.toResponseList(authors.findAllByAuthorBooksIsEmpty());
    }
    public List<BookResponse> books(Long id) {
        find(id);
        return bookMapper.toResponseList(books.findBooksByAuthorId(id));
    }
    @Transactional public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = find(id);
        checkDuplicate(request, id);
        mapper.updateEntity(request, author); // dirty checking, no save()
        return mapper.toResponse(author);
    }
    @Transactional public void delete(Long id) {
        // Cascade deletes AuthorBook links, not the linked Books.
        authors.delete(find(id));
    }
    private void checkDuplicate(AuthorRequest request, Long id) {
        boolean duplicate = id == null
            ? authors.existsByNameIgnoreCaseAndSurnameIgnoreCase(request.name(), request.surname())
            : authors.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(request.name(), request.surname(), id);
        if (duplicate) throw new IllegalStateException("Author already exists");
    }
    private Author find(Long id) {
        return authors.findById(id).orElseThrow(() -> new EntityNotFoundException("Author not found: " + id));
    }

}
