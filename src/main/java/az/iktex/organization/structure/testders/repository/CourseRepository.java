package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Course;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Long id);
    List<Course> findAllByStudentsId(Long studentId);
    long countByStudentsId(Long studentId);
    @Query("select distinct c from Course c left join fetch c.students where c.id = :id")
    Optional<Course> findDetailById(@Param("id") Long id);
    @Query("select c from Course c where c.credit >= :minCredit")
    List<Course> findWithMinCredits(@Param("minCredit") Integer minCredit);
    @Query("select count(s) from Course c join c.students s where c.id = :courseId")
    long countStudentsForCourse(@Param("courseId") Long courseId);

}
