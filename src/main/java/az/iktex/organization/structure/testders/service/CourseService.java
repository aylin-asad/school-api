package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import java.util.ArrayList;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courses;
    private final StudentRepository students;
    private final CourseMapper mapper;
    private final StudentMapper studentMapper;

    @Transactional public CourseResponse create(CourseRequest request) {
        if (courses.existsByTitle(request.title())) throw new IllegalStateException("Course title already exists");
        return mapper.toResponse(courses.save(mapper.toEntity(request)));
    }
    public CourseResponse get(Long id) { return mapper.toResponse(find(id)); }
    public List<CourseResponse> all() { return mapper.toResponseList(courses.findAll()); }
    public List<CourseResponse> byStudent(Long id) {
        if (!students.existsById(id)) throw new EntityNotFoundException("Student not found: " + id);
        return mapper.toResponseList(courses.findAllByStudentsId(id));
    }
    public List<StudentResponse> students(Long id) {
        Course course = courses.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        return studentMapper.toResponseList(new ArrayList<>(course.getStudents()));
    }
    public long studentCount(Long id) {
        find(id);
        return courses.countStudentsForCourse(id);
    }
    @Transactional public CourseResponse update(Long id, CourseRequest request) {
        Course c = find(id);
        if (courses.existsByTitleAndIdNot(request.title(), id))
            throw new IllegalStateException("Course title already exists");
        mapper.updateEntity(request, c); // dirty checking, no save()
        return mapper.toResponse(c);
    }
    @Transactional public void delete(Long id) {
        Course course = courses.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        // Course is inverse; detach on each owning Student side before deleting the course.
        for (Student student : new ArrayList<>(course.getStudents())) student.removeCourse(course);
        courses.delete(course);
    }
    private Course find(Long id) {
        return courses.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
    }

}
