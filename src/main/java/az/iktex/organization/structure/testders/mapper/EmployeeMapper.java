package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    Employee toEntity(EmployeeRequest request);
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    EmployeeResponse toResponse(Employee entity);
    List<EmployeeResponse> toResponseList(List<Employee> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntity(EmployeeRequest request, @MappingTarget Employee entity);

}
