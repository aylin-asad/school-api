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
public class StudentService {

    private final StudentRepository students;
    private final CourseRepository courses;
    private final StudentMapper mapper;

    @Transactional public StudentResponse create(StudentRequest request) {
        return mapper.toResponse(students.save(mapper.toEntity(request)));
    }
    public StudentResponse get(Long id) {
        return mapper.toDetailResponse(students.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id)));
    }
    public List<StudentResponse> all() { return mapper.toResponseList(students.findAll()); }
    public List<StudentResponse> withoutCourses() {
        return mapper.toResponseList(students.findAllByCoursesIsEmpty());
    }
    @Transactional public StudentResponse update(Long id, StudentRequest request) {
        Student student = find(id);
        mapper.updateEntity(request, student); // dirty checking, no save()
        return mapper.toResponse(student);
    }
    @Transactional public void delete(Long id) {
        // Student is the join-table owner; removing it clears only join rows, NOT shared courses.
        students.delete(find(id));
    }
    @Transactional public void enroll(Long studentId, Long courseId) {
        Student s = find(studentId);
        Course c = courses.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        if (students.existsByIdAndCoursesId(studentId, courseId))
            throw new IllegalStateException("Student already enrolled in this course");
        s.addCourse(c);
    }
    @Transactional public void unenroll(Long studentId, Long courseId) {
        Student s = find(studentId);
        Course c = courses.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        if (!students.existsByIdAndCoursesId(studentId, courseId))
            throw new IllegalStateException("Student is not enrolled in this course");
        s.removeCourse(c);
    }
    private Student find(Long id) {
        return students.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id));
    }

}
