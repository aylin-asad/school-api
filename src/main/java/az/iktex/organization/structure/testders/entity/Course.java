package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(name = "uk_course_title", columnNames = "title"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String title;
    @Column(nullable = false) private Integer credit;
    @Column(nullable = false, length = 50) private String teacher;
    @Setter(AccessLevel.NONE)
    // Inverse side: Student.courses owns join-table rows.
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new LinkedHashSet<>();
}
