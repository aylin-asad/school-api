package az.iktex.organization.structure.testders.repository;
import az.iktex.organization.structure.testders.entity.AuthToken;
import az.iktex.organization.structure.testders.enums.TokenType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;
public interface AuthTokenRepository extends JpaRepository<AuthToken,Long>{
 Optional<AuthToken> findByJti(String jti);
 List<AuthToken> findAllBySessionIdAndRevokedFalse(String sid);
 List<AuthToken> findAllByAccountIdAndRevokedFalse(Long id);
 @Query("select t from AuthToken t where t.account.id=:accountId and t.tokenType=:type and t.revoked=false and t.expiresAt>:now order by t.issuedAt desc")
 List<AuthToken> findActive(@Param("accountId") Long accountId,@Param("type") TokenType type,@Param("now") Instant now);
 @Modifying @Query("delete from AuthToken t where t.expiresAt <= :now") int deleteExpired(@Param("now") Instant now);
}
