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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;
    @PostMapping public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        CourseResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/courses/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public CourseResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping public List<CourseResponse> all() { return service.all(); }
    @GetMapping("/student/{studentId}") public List<CourseResponse> byStudent(@PathVariable Long studentId) {
        return service.byStudent(studentId);
    }
    @GetMapping("/{id}/students") public List<StudentResponse> students(@PathVariable Long id) { return service.students(id); }
    @GetMapping("/{id}/students/count") public CountResponse studentCount(@PathVariable Long id) {
        return new CountResponse(id, service.studentCount(id));
    }
    @PutMapping("/{id}") public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
