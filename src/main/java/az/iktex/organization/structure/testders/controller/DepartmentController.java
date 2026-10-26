package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.DepartmentService;
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
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService service;
    @PostMapping public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/departments/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public DepartmentResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping public List<DepartmentResponse> all() { return service.all(); }
    @GetMapping("/without-employees") public List<DepartmentResponse> withoutEmployees() { return service.withoutEmployees(); }
    @PutMapping("/{id}") public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
