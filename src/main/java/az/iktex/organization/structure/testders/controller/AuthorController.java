package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.AuthorService;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService service;
    @PostMapping public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/authors/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public AuthorResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping public List<AuthorResponse> all(@RequestParam(required = false) String country) { return service.all(country); }
    @GetMapping("/without-books") public List<AuthorResponse> withoutBooks() { return service.withoutBooks(); }
    @GetMapping("/{id}/books") public List<BookResponse> books(@PathVariable Long id) { return service.books(id); }
    @PutMapping("/{id}") public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
