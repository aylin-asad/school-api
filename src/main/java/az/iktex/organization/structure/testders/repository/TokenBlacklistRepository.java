package az.iktex.organization.structure.testders.repository;
import az.iktex.organization.structure.testders.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist,Long>{
 boolean existsByJti(String jti); Optional<TokenBlacklist> findByJti(String jti); long countByAccountId(Long accountId);
 @Modifying @Query("delete from TokenBlacklist b where b.expiresAt <= :now") int deleteExpired(@Param("now") Instant now);
 @Modifying @Query(value="insert into token_blacklist(jti,account_id,token_type,reason,expires_at,blacklisted_at) values (:jti,:accountId,:type,:reason,:expiresAt,current_timestamp) on conflict (jti) do nothing",nativeQuery=true)
 int insertIfAbsent(@Param("jti") String jti,@Param("accountId") Long accountId,@Param("type") String type,@Param("reason") String reason,@Param("expiresAt") Instant expiresAt);
}
