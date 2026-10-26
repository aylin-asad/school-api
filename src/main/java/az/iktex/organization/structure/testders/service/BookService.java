package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import java.math.BigDecimal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository books;
    private final AuthorRepository authors;
    private final AuthorBookRepository authorBooks;
    private final BookMapper mapper;

    @Transactional public BookResponse create(BookRequest request) {
        validatePrice(request.price());
        checkTitle(request.title(), null);
        return mapper.toResponse(books.save(mapper.toEntity(request)));
    }
    public BookResponse get(Long id) {
        return mapper.toDetailResponse(books.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id)));
    }
    public List<BookResponse> all(String title) {
        return mapper.toResponseList(title == null ? books.findAll() : books.findAllByTitleContainingIgnoreCase(title));
    }
    public List<BookResponse> cheaperThan(BigDecimal price) {
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must be >= 0");
        return mapper.toResponseList(books.findAllByPriceLessThanEqualOrderByPriceAsc(price));
    }
    @Transactional public BookResponse update(Long id, BookRequest request) {
        Book book = find(id);
        validatePrice(request.price());
        checkTitle(request.title(), id);
        mapper.updateEntity(request, book); // dirty checking, no save()
        return mapper.toResponse(book);
    }
    @Transactional public void delete(Long id) {
        // Orphan links disappear; authors remain.
        books.delete(find(id));
    }
    @Transactional public void linkAuthor(Long bookId, Long authorId) {
        Book book = find(bookId);
        Author author = authors.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Author not found: " + authorId));
        if (authorBooks.existsByAuthorIdAndBookId(authorId, bookId))
            throw new IllegalStateException("Author already linked to book");
        AuthorBook link = new AuthorBook();
        link.setAuthor(author);
        link.setBook(book);
        authorBooks.save(link);
    }
    @Transactional public void unlinkAuthor(Long bookId, Long authorId) {
        // Bulk JPQL deletion avoids loading either inverse collection; affects only the link row.
        if (authorBooks.deletePair(authorId, bookId) == 0)
            throw new EntityNotFoundException("Author-book link does not exist");
    }
    private void checkTitle(String title, Long id) {
        boolean exists = id == null ? books.existsByTitleIgnoreCase(title)
            : books.existsByTitleIgnoreCaseAndIdNot(title, id);
        if (exists) throw new IllegalStateException("Book title already exists");
    }
    private void validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must be >= 0");
    }
    private Book find(Long id) {
        return books.findById(id).orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
    }

}
