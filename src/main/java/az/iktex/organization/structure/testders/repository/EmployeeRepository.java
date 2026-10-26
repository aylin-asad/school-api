package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Employee;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.math.BigDecimal;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByDepartmentId(Long departmentId);
    boolean existsByDepartmentIdAndNameIgnoreCaseAndSurnameIgnoreCase(Long departmentId, String name, String surname);
    boolean existsByDepartmentIdAndNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(Long departmentId, String name, String surname, Long id);
    List<Employee> findAllByDepartmentId(Long departmentId);
    List<Employee> findAllBySalaryBetween(BigDecimal min, BigDecimal max);
    @Query("select avg(e.salary) from Employee e where e.department.id = :departmentId")
    Double averageSalary(@Param("departmentId") Long departmentId);
    @Query("select e from Employee e join fetch e.department where e.id = :id")
    Optional<Employee> findDetailById(@Param("id") Long id);

}
