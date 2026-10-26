package az.iktex.organization.structure.testders.entity;

import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.entity.converter.GenderConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50) private String name;
    @Column(nullable = false, length = 50) private String surname;
    @Column(nullable = false, length = 100) private String email;
    @Convert(converter = GenderConverter.class)
    @Column(name = "gender_id", nullable = false) private Gender gender;

    // mappedBy: Passport owns the FK. Cascade and orphanRemoval manage its lifecycle.
    @Setter(AccessLevel.NONE)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Passport passport;

    public void setPassport(Passport newPassport) {
        if (this.passport == newPassport) return;
        Passport previous = this.passport;
        this.passport = null;
        if (previous != null) previous.setUser(null);
        this.passport = newPassport;
        if (newPassport != null) newPassport.setUser(this);
    }
}
