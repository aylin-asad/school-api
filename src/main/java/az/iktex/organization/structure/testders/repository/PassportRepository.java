package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Passport;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface PassportRepository extends JpaRepository<Passport, Long> {
    // ORM / derived queries and explicit JPQL queries

    boolean existsBySerialNumber(String serialNumber);
    boolean existsBySerialNumberAndIdNot(String serialNumber, Long id);
    boolean existsByUserId(Long userId);
    Optional<Passport> findByUserId(Long userId);
    @Query("select p from Passport p join fetch p.user where p.id = :id")
    Optional<Passport> findWithUserById(@Param("id") Long id);
    @Query("select p from Passport p where lower(p.country) = lower(:country)")
    List<Passport> findAllForCountry(@Param("country") String country);

}
