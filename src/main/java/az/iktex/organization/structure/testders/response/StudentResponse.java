package az.iktex.organization.structure.testders.response;

import java.util.List;
public record StudentResponse(Long id, String name, String surname, Integer age, List<CourseResponse> courses) {}
