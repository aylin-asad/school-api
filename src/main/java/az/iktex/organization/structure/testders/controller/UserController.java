package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.UserService;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import az.iktex.organization.structure.testders.enums.Gender;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="User", description="User management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/users/" + out.id())).body(out);
    }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}") public UserResponse get(@PathVariable Long id) { return service.get(id); }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/by-email") public UserResponse byEmail(@RequestParam String email) { return service.byEmail(email); }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping public List<UserResponse> all(@RequestParam(required = false) Gender gender) { return service.all(gender); }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/without-passport") public List<UserResponse> withoutPassport() { return service.withoutPassport(); }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PutMapping("/{id}") public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return service.update(id, request);
    }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping("/{id}/passport") public ResponseEntity<PassportResponse> addPassport(
        @PathVariable Long id, @Valid @RequestBody PassportRequest request) {
        PassportResponse out = service.addPassport(id, request);
        return ResponseEntity.created(URI.create("/test-demo/api/users/" + id + "/passport")).body(out);
    }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}/passport") public PassportResponse passport(@PathVariable Long id) { return service.passport(id); }
    @Operation(summary="User operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{id}/passport") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassport(@PathVariable Long id) { service.deletePassport(id); }

}
