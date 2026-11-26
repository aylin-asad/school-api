package az.iktex.organization.structure.testders.service;
import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.enums.TokenType;
import az.iktex.organization.structure.testders.repository.AuthTokenRepository;
import az.iktex.organization.structure.testders.response.AuthResponse;
import az.iktex.organization.structure.testders.security.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
@Service @RequiredArgsConstructor
public class TokenService {
 private final JwtService jwt; private final AuthTokenRepository tokens;
 @Transactional public AuthResponse issuePair(Account a,String sid){
  return issuePair(a,sid,java.time.Instant.now());
 }
 @Transactional public AuthResponse issuePair(Account a,String sid,java.time.Instant loginAt){
  var access=jwt.generate(a,TokenType.ACCESS,sid);var refresh=jwt.generate(a,TokenType.REFRESH,sid);
  store(a,sid,access,loginAt);store(a,sid,refresh,loginAt);
  return new AuthResponse("Bearer",access.value(),Duration.between(access.issuedAt(),access.expiresAt()).toSeconds(),refresh.value(),Duration.between(refresh.issuedAt(),refresh.expiresAt()).toSeconds());
 }
 private void store(Account a,String sid,IssuedToken issued,java.time.Instant loginAt){AuthToken t=new AuthToken();t.setAccount(a);t.setSessionId(sid);t.setJti(issued.jti());t.setTokenType(issued.type());t.setIssuedAt(issued.issuedAt());t.setLoginAt(loginAt);t.setExpiresAt(issued.expiresAt());tokens.save(t);}
}
