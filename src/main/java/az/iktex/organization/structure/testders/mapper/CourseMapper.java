package az.iktex.organization.structure.testders.mapper;

import org.mapstruct.*;
import java.util.List;

import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    Course toEntity(CourseRequest request);
    CourseResponse toResponse(Course entity);
    List<CourseResponse> toResponseList(List<Course> entities);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    void updateEntity(CourseRequest request, @MappingTarget Course entity);

}
