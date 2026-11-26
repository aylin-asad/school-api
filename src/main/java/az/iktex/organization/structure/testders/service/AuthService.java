package az.iktex.organization.structure.testders.service;
import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.enums.*;
import az.iktex.organization.structure.testders.exception.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.security.jwt.JwtService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
@Service @RequiredArgsConstructor
public class AuthService {
 private final AccountRepository accounts;private final AuthTokenRepository tokens;private final TokenBlacklistRepository blacklist;
 private final TokenBlacklistService revocations;private final TokenService issuer;private final JwtService jwt;private final AuthenticationManager authenticationManager;private final PasswordEncoder encoder;
 private Account lock(Long id){return accounts.lockById(id).orElseThrow(()->new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));}
 private Account enabled(Account a){if(!a.isEnabled())throw new ForbiddenException(ErrorCode.AUTH_ACCOUNT_DISABLED);return a;}
 @Transactional public AuthResponse login(LoginRequest req){
  var candidate=accounts.findByUsernameOrEmail(req.login(),req.login());
  // Serialize account modifications and issuance, including simultaneous refresh/logout.
  Account a=candidate.map(x->lock(x.getId())).orElse(null);
  var authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.login(),req.password()));
  if(a==null)a=lock(accounts.findByUsername(authentication.getName()).orElseThrow(()->new BadCredentialsException("Invalid credentials")).getId());
  return issuer.issuePair(enabled(a),UUID.randomUUID().toString());
 }
 // Reuse detection deliberately commits revocations even though a 401 is returned.
 @Transactional(noRollbackFor=UnauthorizedException.class) public AuthResponse refresh(RefreshRequest req){
  Claims c;
  try{c=jwt.parse(req.refreshToken());}catch(ExpiredJwtException ex){throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_EXPIRED);}catch(JwtException|IllegalArgumentException ex){throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_INVALID);}
  if(!"REFRESH".equals(c.get("type",String.class)))throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_TYPE_MISMATCH);
  Account a=lock(accounts.findByUsername(c.getSubject()).orElseThrow(()->new ForbiddenException(ErrorCode.AUTH_ACCOUNT_DISABLED)).getId());
  var blocked=blacklist.findByJti(c.getId());
  if(blocked.isPresent()){
   if(blocked.get().getReason()==RevokeReason.REFRESH_ROTATION){revocations.revokeAll(a.getId(),RevokeReason.REFRESH_REUSE_DETECTED);throw new UnauthorizedException(ErrorCode.AUTH_REFRESH_REUSED);}
   throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_REVOKED);
  }
  enabled(a);
  String sid=c.get("sid",String.class);
  var t=tokens.findByJti(c.getId()).orElseThrow(()->new UnauthorizedException(ErrorCode.AUTH_TOKEN_INVALID));
  if(t.isRevoked()||t.getTokenType()!=TokenType.REFRESH||!t.getAccount().getId().equals(a.getId())||!t.getSessionId().equals(sid))throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_REVOKED);
  revocations.revokeSession(sid,RevokeReason.REFRESH_ROTATION);
  return issuer.issuePair(a,sid,t.getLoginAt());
 }
 @Transactional public void logout(AuthPrincipal p){lock(p.accountId());revocations.revokeSession(p.sid(),RevokeReason.LOGOUT);}
 @Transactional public void logoutAll(AuthPrincipal p){lock(p.accountId());revocations.revokeAll(p.accountId(),RevokeReason.LOGOUT_ALL);}
 @Transactional(readOnly=true) public AccountResponse me(AuthPrincipal p){return AccountResponse.from(accounts.findById(p.accountId()).orElseThrow(()->new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND)));}
 @Transactional(readOnly=true) public List<SessionResponse> sessions(AuthPrincipal p){return tokens.findActive(p.accountId(),TokenType.REFRESH,Instant.now()).stream().map(t->new SessionResponse(t.getSessionId(),t.getLoginAt(),t.getExpiresAt(),t.getSessionId().equals(p.sid()))).toList();}
 @Transactional public void revoke(AuthPrincipal p,String sid){lock(p.accountId());if(tokens.findActive(p.accountId(),TokenType.REFRESH,Instant.now()).stream().noneMatch(t->t.getSessionId().equals(sid)))throw new NotFoundException(ErrorCode.SESSION_NOT_FOUND);revocations.revokeSession(sid,RevokeReason.SESSION_REVOKED);}
 @Transactional public void password(AuthPrincipal p,ChangePasswordRequest req){Account a=enabled(lock(p.accountId()));
  if(!encoder.matches(req.oldPassword(),a.getPassword()))throw new InvalidRequestException(ErrorCode.AUTH_OLD_PASSWORD_WRONG);
  if(encoder.matches(req.newPassword(),a.getPassword()))throw new InvalidRequestException(ErrorCode.AUTH_PASSWORD_SAME);
  a.setPassword(encoder.encode(req.newPassword()));a.setPasswordChangedAt(Instant.now());accounts.save(a);revocations.revokeAll(a.getId(),RevokeReason.PASSWORD_CHANGED);
 }
}
