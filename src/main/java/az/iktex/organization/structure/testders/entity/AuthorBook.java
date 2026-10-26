package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "author_book", uniqueConstraints =
    @UniqueConstraint(name = "uk_author_book", columnNames = {"author_id", "book_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthorBook {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    // No cascade: deleting an association must never remove its author or book.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ab_author"))
    private Author author;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ab_book"))
    private Book book;
}
