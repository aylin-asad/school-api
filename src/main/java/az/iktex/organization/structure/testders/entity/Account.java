package az.iktex.organization.structure.testders.entity;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.entity.converter.RoleConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
@Entity @Table(name="accounts",uniqueConstraints={@UniqueConstraint(name="uk_account_username",columnNames="username"),@UniqueConstraint(name="uk_account_email",columnNames="email")})
@Getter @Setter @NoArgsConstructor
public class Account {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=50) private String username;
 @Column(nullable=false,length=100) private String email;
 @Column(nullable=false,length=100) private String password;
 @Convert(converter=RoleConverter.class) @Column(name="role_id",nullable=false) private Role role;
 @Column(nullable=false) private boolean enabled=true;
 private Instant passwordChangedAt;
 @CreationTimestamp @Column(nullable=false) private Instant createdAt;
}
