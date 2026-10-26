package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.BookService;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;
    @PostMapping public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        BookResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/books/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public BookResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping public List<BookResponse> all(@RequestParam(required = false) String title) { return service.all(title); }
    @GetMapping("/cheaper-than") public List<BookResponse> cheaperThan(@RequestParam BigDecimal price) {
        return service.cheaperThan(price);
    }
    @PutMapping("/{id}") public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{bookId}/authors/{authorId}") public ResponseEntity<Void> linkAuthor(
        @PathVariable Long bookId, @PathVariable Long authorId) {
        service.linkAuthor(bookId, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/{bookId}/authors/{authorId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlinkAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        service.unlinkAuthor(bookId, authorId);
    }

}
