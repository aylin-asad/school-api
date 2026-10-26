package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.AuthorBook;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AuthorBookRepository extends JpaRepository<AuthorBook, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByAuthorIdAndBookId(Long authorId, Long bookId);
    Optional<AuthorBook> findByAuthorIdAndBookId(Long authorId, Long bookId);
    List<AuthorBook> findAllByAuthorId(Long authorId);
    List<AuthorBook> findAllByBookId(Long bookId);
    @Query("select ab from AuthorBook ab join fetch ab.book where ab.author.id = :authorId")
    List<AuthorBook> findWithBooksByAuthorId(@Param("authorId") Long authorId);
    @Query("select ab from AuthorBook ab join fetch ab.author where ab.book.id = :bookId")
    List<AuthorBook> findWithAuthorsByBookId(@Param("bookId") Long bookId);
    @Modifying
    @Query("delete from AuthorBook ab where ab.author.id = :authorId and ab.book.id = :bookId")
    int deletePair(@Param("authorId") Long authorId, @Param("bookId") Long bookId);

}
