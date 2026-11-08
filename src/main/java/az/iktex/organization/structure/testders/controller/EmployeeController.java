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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Employee", description="Employee management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PostMapping public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse out = service.create(request);
        return ResponseEntity.created(URI.create("/test-demo/api/employees/" + out.id())).body(out);
    }
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/{id}") public EmployeeResponse get(@PathVariable Long id) { return service.get(id); }
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/department/{departmentId}") public List<EmployeeResponse> byDepartment(@PathVariable Long departmentId) {
        return service.byDepartment(departmentId);
    }
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/salary-range") public List<EmployeeResponse> salaryRange(
        @RequestParam BigDecimal min, @RequestParam BigDecimal max) { return service.salaryRange(min, max); }
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @GetMapping("/department/{id}/average-salary") public AverageSalaryResponse averageSalary(@PathVariable Long id) {
        return service.averageSalary(id);
    }
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @PutMapping("/{id}") public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return service.update(id, request);
    }
    @Operation(summary="Employee operation", description="Localized errors: 400 ERR-1000, 404 ERR-1006, 409 ERR-1007, 500 ERR-1001")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
