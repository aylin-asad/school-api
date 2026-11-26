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
 public boolean isBlacklisted(String jti){return blacklist.existsByJti(jti);}
 @Transactional public void blacklist(AuthToken t,RevokeReason reason){
  if(t.isRevoked())return;
  Instant now=Instant.now();t.setRevoked(true);t.setRevokedAt(now);t.setRevokeReason(reason);tokens.save(t);
  // PostgreSQL ON CONFLICT avoids poisoning the transaction on parallel revocations.
  if(t.getExpiresAt().isAfter(now))blacklist.insertIfAbsent(t.getJti(),t.getAccount().getId(),t.getTokenType().name(),reason.name(),t.getExpiresAt());
 }
 @Transactional public void revokeSession(String sid,RevokeReason reason){tokens.findAllBySessionIdAndRevokedFalse(sid).forEach(t->blacklist(t,reason));}
 @Transactional public void revokeAll(Long accountId,RevokeReason reason){tokens.findAllByAccountIdAndRevokedFalse(accountId).forEach(t->blacklist(t,reason));}
}
