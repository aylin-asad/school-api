package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Department;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    List<Department> findAllByEmployeesIsEmpty();
    List<Department> findAllByNameContainingIgnoreCase(String name);
    @Query("select distinct d from Department d left join fetch d.employees where d.id = :id")
    Optional<Department> findDetailById(@Param("id") Long id);
    @Query("select d from Department d where d.employees is empty")
    List<Department> findEmptyDepartments();

}
