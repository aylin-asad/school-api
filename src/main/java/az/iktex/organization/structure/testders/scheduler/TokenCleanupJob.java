package az.iktex.organization.structure.testders.scheduler;
import az.iktex.organization.structure.testders.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;
@Component @RequiredArgsConstructor @Slf4j
public class TokenCleanupJob {
 private final TokenBlacklistRepository blacklistRepository;private final AuthTokenRepository authTokenRepository;
 @Transactional @Scheduled(cron="${app.security.token-cleanup-cron}") public void cleanup(){
  MDC.put("traceId","cleanup-"+UUID.randomUUID());
  try{Instant now=Instant.now();int b=blacklistRepository.deleteExpired(now);int t=authTokenRepository.deleteExpired(now);log.info("Token cleanup: token_blacklist={}, auth_tokens={} rows deleted",b,t);}finally{MDC.remove("traceId");}
 }
}
