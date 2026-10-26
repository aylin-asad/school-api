package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "books", uniqueConstraints = @UniqueConstraint(name = "uk_book_title", columnNames = "title"))
@Check(name = "chk_book_price", constraints = "price >= 0")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String title;
    @Column(nullable = false, precision = 8, scale = 2) private BigDecimal price;
    @Column(name = "page_count", nullable = false) private Integer pageCount;
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorBook> authorBooks = new ArrayList<>();
}
