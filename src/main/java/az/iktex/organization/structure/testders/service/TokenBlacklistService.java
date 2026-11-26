package az.iktex.organization.structure.testders.service;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.entity.AuthToken;
import az.iktex.organization.structure.testders.enums.RevokeReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
@Service @RequiredArgsConstructor
public class TokenBlacklistService {
 private final TokenBlacklistRepository blacklist; private final AuthTokenRepository tokens;
 private final com.github.benmanes.caffeine.cache.Cache<String,Boolean> positiveCache=com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(java.time.Duration.ofMinutes(1)).maximumSize(10000).build();
 public boolean isBlacklisted(String jti){
  if(Boolean.TRUE.equals(positiveCache.getIfPresent(jti)))return true;
  boolean found=blacklist.existsByJti(jti);if(found)positiveCache.put(jti,true);return found;
 }
 @Transactional public void blacklist(AuthToken t,RevokeReason reason){
  if(t.isRevoked())return;
  Instant now=Instant.now();t.setRevoked(true);t.setRevokedAt(now);t.setRevokeReason(reason);tokens.save(t);
  // PostgreSQL ON CONFLICT avoids poisoning the transaction on parallel revocations.
  if(t.getExpiresAt().isAfter(now)){
   blacklist.insertIfAbsent(t.getJti(),t.getAccount().getId(),t.getTokenType().name(),reason.name(),t.getExpiresAt());
   // Update only after commit; never cache a revocation that could roll back.
   org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization(){
    @Override public void afterCommit(){positiveCache.put(t.getJti(),true);}
   });
  }
 }
 @Transactional public void revokeSession(String sid,RevokeReason reason){tokens.findAllBySessionIdAndRevokedFalse(sid).forEach(t->blacklist(t,reason));}
 @Transactional public void revokeAll(Long accountId,RevokeReason reason){tokens.findAllByAccountIdAndRevokedFalse(accountId).forEach(t->blacklist(t,reason));}
}
