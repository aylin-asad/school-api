package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
import az.iktex.organization.structure.testders.enums.Gender;

public interface UserRepository extends JpaRepository<User, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<User> findByEmail(String email);
    List<User> findAllByGender(Gender gender);
    List<User> findAllByPassportIsNull();
    @Query("select u from User u left join fetch u.passport where u.id = :id")
    Optional<User> findDetailById(@Param("id") Long id);
    @Query("select u from User u where u.gender = :gender order by u.surname, u.name")
    List<User> findSortedByGender(@Param("gender") Gender gender);

}
