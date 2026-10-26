package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Department toEntity(DepartmentRequest request);
    @Named("summary")
    @Mapping(target = "employees", ignore = true)
    DepartmentResponse toResponse(Department entity);
    @Named("withEmployees")
    @Mapping(target = "employees", source = "employees")
    DepartmentResponse toDetailResponse(Department entity);
    @IterableMapping(qualifiedByName = "summary")
    List<DepartmentResponse> toResponseList(List<Department> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntity(DepartmentRequest request, @MappingTarget Department entity);

}
