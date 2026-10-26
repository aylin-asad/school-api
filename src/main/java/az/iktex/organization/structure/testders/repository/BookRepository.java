package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Book;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.math.BigDecimal;

public interface BookRepository extends JpaRepository<Book, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByTitleIgnoreCase(String title);
    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);
    List<Book> findAllByTitleContainingIgnoreCase(String title);
    List<Book> findAllByPriceLessThanEqualOrderByPriceAsc(BigDecimal price);
    @Query("select distinct b from Book b left join fetch b.authorBooks ab left join fetch ab.author where b.id = :id")
    Optional<Book> findDetailById(@Param("id") Long id);
    @Query("select distinct b from Book b join b.authorBooks ab where ab.author.id = :authorId")
    List<Book> findBooksByAuthorId(@Param("authorId") Long authorId);

}
