package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Author;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);
    boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(String name, String surname, Long id);
    List<Author> findAllByCountryIgnoreCase(String country);
    List<Author> findAllByAuthorBooksIsEmpty();
    @Query("select distinct a from Author a left join fetch a.authorBooks ab left join fetch ab.book where a.id = :id")
    Optional<Author> findDetailById(@Param("id") Long id);
    @Query("select a from Author a where a.authorBooks is empty order by a.surname")
    List<Author> findAuthorsWithoutBooks();

}
