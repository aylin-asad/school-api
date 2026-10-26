package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "employees")
@Check(name = "chk_employee_salary", constraints = "salary > 0")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String name;
    @Column(nullable = false, length = 50) private String surname;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal salary;
    // No cascade from Employee to Department: deleting a worker must not delete its department.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;
}
