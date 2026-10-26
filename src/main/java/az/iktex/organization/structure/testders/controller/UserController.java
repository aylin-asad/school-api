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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    @PostMapping public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/users/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public UserResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/by-email") public UserResponse byEmail(@RequestParam String email) { return service.byEmail(email); }
    @GetMapping public List<UserResponse> all(@RequestParam(required = false) Gender gender) { return service.all(gender); }
    @GetMapping("/without-passport") public List<UserResponse> withoutPassport() { return service.withoutPassport(); }
    @PutMapping("/{id}") public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/passport") public ResponseEntity<PassportResponse> addPassport(
        @PathVariable Long id, @Valid @RequestBody PassportRequest request) {
        PassportResponse out = service.addPassport(id, request);
        return ResponseEntity.created(URI.create("/test-demo/api/users/" + id + "/passport")).body(out);
    }
    @GetMapping("/{id}/passport") public PassportResponse passport(@PathVariable Long id) { return service.passport(id); }
    @DeleteMapping("/{id}/passport") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassport(@PathVariable Long id) { service.deletePassport(id); }

}
