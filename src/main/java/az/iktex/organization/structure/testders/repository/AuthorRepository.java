
package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Author;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCase(
            String name, String surname);

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(
            String name, String surname, Long id);

    // Hibernate Query Cache
    @QueryHints(@QueryHint(
            name = "org.hibernate.cacheable",
            value = "true"
    ))
    List<Author> findAllByCountryIgnoreCase(String country);

    List<Author> findAllByAuthorBooksIsEmpty();

    @Query("select distinct a from Author a left join fetch a.authorBooks ab left join fetch ab.book where a.id = :id")
    Optional<Author> findDetailById(@Param("id") Long id);

    @Query("select a from Author a where a.authorBooks is empty order by a.surname")
    List<Author> findAuthorsWithoutBooks();
}
