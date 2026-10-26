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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;
    @PostMapping public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/students/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public StudentResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping public List<StudentResponse> all() { return service.all(); }
    @GetMapping("/without-courses") public List<StudentResponse> withoutCourses() { return service.withoutCourses(); }
    @PutMapping("/{id}") public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<Void> enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        service.enroll(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/{studentId}/courses/{courseId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        service.unenroll(studentId, courseId);
    }

}
