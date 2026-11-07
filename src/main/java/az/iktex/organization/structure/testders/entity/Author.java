package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor

@Cacheable
@org.hibernate.annotations.Cache(
        usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE,
        region = "authors"
)

public class Author {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String name;
    @Column(nullable = false, length = 50) private String surname;
    @Column(length = 50) private String country;
    @Setter(AccessLevel.NONE)
    // Removing an author removes only join entities, not Book objects.

    @org.hibernate.annotations.Cache(
            usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE,
            region = "author-authorBooks"
    )

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorBook> authorBooks = new ArrayList<>();
}
