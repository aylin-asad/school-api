package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.CourseService;
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

@Tag(name="Course", description="Course management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        CourseResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/courses/" + out.id())).body(out);
    }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}") public CourseResponse get(@PathVariable Long id) { return service.get(id); }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping public List<CourseResponse> all() { return service.all(); }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/student/{studentId}") public List<CourseResponse> byStudent(@PathVariable Long studentId) {
        return service.byStudent(studentId);
    }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}/students") public List<StudentResponse> students(@PathVariable Long id) { return service.students(id); }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}/students/count") public CountResponse studentCount(@PathVariable Long id) {
        return new CountResponse(id, service.studentCount(id));
    }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PutMapping("/{id}") public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return service.update(id, request);
    }
    @Operation(summary="Course operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
