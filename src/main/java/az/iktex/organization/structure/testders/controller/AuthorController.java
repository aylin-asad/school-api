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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Author", description="Author management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService service;
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/authors/" + out.id())).body(out);
    }
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}") public AuthorResponse get(@PathVariable Long id) { return service.get(id); }
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping public List<AuthorResponse> all(@RequestParam(required = false) String country) { return service.all(country); }
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/without-books") public List<AuthorResponse> withoutBooks() { return service.withoutBooks(); }
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}/books") public List<BookResponse> books(@PathVariable Long id) { return service.books(id); }
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PutMapping("/{id}") public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return service.update(id, request);
    }
    @Operation(summary="Author operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
