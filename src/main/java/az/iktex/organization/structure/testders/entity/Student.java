package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String name;
    @Column(nullable = false, length = 50) private String surname;
    @Column(nullable = false) private Integer age;

    // Do NOT cascade REMOVE: one student leaving must not delete shared Course entities.
    @Setter(AccessLevel.NONE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "student_course",
        joinColumns = @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_sc_student")),
        inverseJoinColumns = @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "fk_sc_course")),
        uniqueConstraints = @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"}))
    private Set<Course> courses = new LinkedHashSet<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }
    public void removeCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this);
    }
}
