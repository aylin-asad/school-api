package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;

@Mapper(componentModel = "spring", uses = CourseMapper.class)
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Student toEntity(StudentRequest request);
    @Named("summary")
    @Mapping(target = "courses", ignore = true)
    StudentResponse toResponse(Student entity);
    @Named("withCourses")
    @Mapping(target = "courses", source = "courses")
    StudentResponse toDetailResponse(Student entity);
    @IterableMapping(qualifiedByName = "summary")
    List<StudentResponse> toResponseList(List<Student> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntity(StudentRequest request, @MappingTarget Student entity);

}
