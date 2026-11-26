package az.iktex.organization.structure.testders.repository;
import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.Role;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface AccountRepository extends JpaRepository<Account,Long>{
 Optional<Account> findByUsername(String username);
 Optional<Account> findByUsernameOrEmail(String username,String email);
 boolean existsByUsername(String username);
 boolean existsByEmail(String email);
 boolean existsByRole(Role role);
 List<Account> findAllByRole(Role role);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Account a where a.username=:login or a.email=:login") Optional<Account> lockByLogin(@Param("login") String login);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Account a where a.username=:username") Optional<Account> lockByUsername(@Param("username") String username);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Account a where a.id=:id") Optional<Account> lockById(@Param("id") Long id);
}
