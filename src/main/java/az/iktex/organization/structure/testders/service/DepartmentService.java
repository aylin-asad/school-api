package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departments;
    private final EmployeeRepository employees;
    private final DepartmentMapper mapper;

    @Transactional public DepartmentResponse create(DepartmentRequest request) {
        if (departments.existsByName(request.name())) throw new IllegalStateException("Department name already exists");
        return mapper.toResponse(departments.save(mapper.toEntity(request)));
    }
    public DepartmentResponse get(Long id) {
        return mapper.toDetailResponse(departments.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id)));
    }
    public List<DepartmentResponse> all() { return mapper.toResponseList(departments.findAll()); }
    public List<DepartmentResponse> withoutEmployees() {
        return mapper.toResponseList(departments.findAllByEmployeesIsEmpty());
    }
    @Transactional public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department d = find(id);
        if (departments.existsByNameAndIdNot(request.name(), id))
            throw new IllegalStateException("Department name already exists");
        mapper.updateEntity(request, d); // dirty checking, no save()
        return mapper.toResponse(d);
    }
    @Transactional public void delete(Long id) {
        Department d = find(id);
        // Despite cascade ALL, deleting a non-empty department is forbidden by the service rule.
        if (employees.existsByDepartmentId(id)) throw new IllegalStateException("Cannot delete department with employees");
        departments.delete(d);
    }
    private Department find(Long id) {
        return departments.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
    }

}
