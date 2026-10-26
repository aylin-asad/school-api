package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.EmployeeService;
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
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;
    @PostMapping public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/employees/" + out.id())).body(out);
    }
    @GetMapping("/{id}") public EmployeeResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/department/{departmentId}") public List<EmployeeResponse> byDepartment(@PathVariable Long departmentId) {
        return service.byDepartment(departmentId);
    }
    @GetMapping("/salary-range") public List<EmployeeResponse> salaryRange(
        @RequestParam BigDecimal min, @RequestParam BigDecimal max) { return service.salaryRange(min, max); }
    @GetMapping("/department/{id}/average-salary") public AverageSalaryResponse averageSalary(@PathVariable Long id) {
        return service.averageSalary(id);
    }
    @PutMapping("/{id}") public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
