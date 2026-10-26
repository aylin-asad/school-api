package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Student;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByIdAndCoursesId(Long id, Long courseId);
    List<Student> findAllByCoursesIsEmpty();
    List<Student> findAllByAgeGreaterThanEqual(Integer age);
    List<Student> findAllBySurnameIgnoreCase(String surname);
    @Query("select distinct s from Student s left join fetch s.courses where s.id = :id")
    Optional<Student> findDetailById(@Param("id") Long id);
    @Query("select s from Student s where s.courses is empty")
    List<Student> findStudentsWithoutCourses();

}
