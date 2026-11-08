package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.StudentService;
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

@Tag(name="Student", description="Student management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/students/" + out.id())).body(out);
    }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}") public StudentResponse get(@PathVariable Long id) { return service.get(id); }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping public List<StudentResponse> all() { return service.all(); }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/without-courses") public List<StudentResponse> withoutCourses() { return service.withoutCourses(); }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PutMapping("/{id}") public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<Void> enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        service.enroll(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary="Student operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{studentId}/courses/{courseId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        service.unenroll(studentId, courseId);
    }

}
