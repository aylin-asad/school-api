package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import java.math.BigDecimal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final EmployeeMapper mapper;

    @Transactional public EmployeeResponse create(EmployeeRequest request) {
        checkSalary(request.salary());
        Department department = findDepartment(request.departmentId());
        checkDuplicate(request.departmentId(), request.name(), request.surname(), null);
        Employee employee = mapper.toEntity(request);
        employee.setDepartment(department);
        return mapper.toResponse(employees.save(employee));
    }
    public EmployeeResponse get(Long id) { return mapper.toResponse(find(id)); }
    public List<EmployeeResponse> byDepartment(Long id) {
        findDepartment(id);
        return mapper.toResponseList(employees.findAllByDepartmentId(id));
    }
    public List<EmployeeResponse> salaryRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null || min.compareTo(max) > 0)
            throw new IllegalArgumentException("Invalid salary range: min must be <= max");
        return mapper.toResponseList(employees.findAllBySalaryBetween(min, max));
    }
    public AverageSalaryResponse averageSalary(Long departmentId) {
        findDepartment(departmentId);
        return new AverageSalaryResponse(departmentId, employees.averageSalary(departmentId));
    }
    @Transactional public EmployeeResponse update(Long id, EmployeeRequest request) {
        checkSalary(request.salary());
        Employee employee = find(id);
        Department newDepartment = findDepartment(request.departmentId());
        checkDuplicate(request.departmentId(), request.name(), request.surname(), id);
        mapper.updateEntity(request, employee);
        // Re-parent via owning FK. Removing from oldDepartment.employees would orphan-delete it.
        employee.setDepartment(newDepartment);
        return mapper.toResponse(employee); // dirty checking, no save()
    }
    @Transactional public void delete(Long id) { employees.delete(find(id)); }
    private Employee find(Long id) {
        return employees.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }
    private Department findDepartment(Long id) {
        return departments.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
    }
    private void checkDuplicate(Long dep, String name, String surname, Long exclude) {
        boolean exists = exclude == null
            ? employees.existsByDepartmentIdAndNameIgnoreCaseAndSurnameIgnoreCase(dep, name, surname)
            : employees.existsByDepartmentIdAndNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(dep, name, surname, exclude);
        if (exists) throw new IllegalStateException("Employee with this name already exists in department");
    }
    private void checkSalary(BigDecimal salary) {
        if (salary == null || salary.signum() <= 0) throw new IllegalArgumentException("Salary must be positive");
    }

}
