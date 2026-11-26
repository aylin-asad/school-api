package az.iktex.organization.structure.testders.service;
import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import az.iktex.organization.structure.testders.enums.*;
import az.iktex.organization.structure.testders.exception.*;
import az.iktex.organization.structure.testders.security.*;
import az.iktex.organization.structure.testders.security.jwt.*;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.*;
import io.jsonwebtoken.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
class AuthServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 AccountRepository r=mock(AccountRepository.class);
    AuthTokenRepository t=mock(AuthTokenRepository.class);
    TokenBlacklistRepository b=mock(TokenBlacklistRepository.class);
    TokenBlacklistService rev=mock(TokenBlacklistService.class);
    TokenService issuer=mock(TokenService.class);
    JwtService jwt=mock(JwtService.class);
    AuthenticationManager am=mock(AuthenticationManager.class);
    PasswordEncoder enc=mock(PasswordEncoder.class);
    AuthService s=new AuthService(r,t,b,rev,issuer,jwt,am,enc);
 Account a=new Account();
    AuthPrincipal p=new AuthPrincipal(1L,"user",Role.USER,"jti","sid");
    LoginRequest login=new LoginRequest("user","pass");
    RefreshRequest refresh=new RefreshRequest("refresh");
    AuthResponse response=new AuthResponse("Bearer","a",900,"r",604800);
 @BeforeEach void setup(){
        a.setId(1L);
        a.setUsername("user");
        a.setPassword("hash");
        a.setRole(Role.USER);
    }
 @Test void loginSuccessAndReset(){
        when(r.lockByLogin("user")).thenReturn(Optional.of(a));
        Authentication auth=mock(Authentication.class);
        when(am.authenticate(any())).thenReturn(auth);
        when(issuer.issuePair(eq(a),anyString())).thenReturn(response);
        assertSame(response,s.login(login));
        a.setLockedUntil(Instant.now().plusSeconds(600));
        s.login(login);
        a.setLockedUntil(Instant.now().minusSeconds(1));
        a.setFailedLoginAttempts(5);
        s.login(login);
        verify(r).saveAndFlush(a);
        assertEquals(0,a.getFailedLoginAttempts());
        assertNull(a.getLockedUntil());
        a.setEnabled(false);
        fails(ErrorCode.AUTH_ACCOUNT_DISABLED,()->s.login(login));
    }
 @Test void loginFallback(){
        Authentication auth=mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(am.authenticate(any())).thenReturn(auth);
        assertThrows(BadCredentialsException.class,()->s.login(login));
        when(r.lockByUsername("user")).thenReturn(Optional.of(a));
        when(issuer.issuePair(eq(a),anyString())).thenReturn(response);
        assertSame(response,s.login(login));
    }
 @Test void badLogin(){
        when(am.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        assertThrows(BadCredentialsException.class,()->s.login(login));
        when(r.lockByLogin("user")).thenReturn(Optional.of(a));
        a.setEnabled(false);
        assertThrows(BadCredentialsException.class,()->s.login(login));
        assertEquals(0,a.getFailedLoginAttempts());
        a.setEnabled(true);
        assertThrows(BadCredentialsException.class,()->s.login(login));
        assertEquals(1,a.getFailedLoginAttempts());
        a.setFailedLoginAttempts(4);
        assertThrows(LockedException.class,()->s.login(login));
        assertNotNull(a.getLockedUntil());
    }
 Claims claims(){
        Claims c=mock(Claims.class);
        when(jwt.parse("refresh")).thenReturn(c);
        when(c.get("type",String.class)).thenReturn("REFRESH");
        when(c.getSubject()).thenReturn("user");
        when(c.getId()).thenReturn("jti");
        when(c.get("sid",String.class)).thenReturn("sid");
        when(c.getExpiration()).thenReturn(Date.from(Instant.now().plusSeconds(600)));
        return c;
    }
 @Test void refreshParseErrors(){
        when(jwt.parse("refresh")).thenThrow(new ExpiredJwtException(null,null,"expired"));
        fails(ErrorCode.AUTH_TOKEN_EXPIRED,()->s.refresh(refresh));
        doThrow(new JwtException("bad")).when(jwt).parse("refresh");
        fails(ErrorCode.AUTH_TOKEN_INVALID,()->s.refresh(refresh));
        doThrow(new IllegalArgumentException()).when(jwt).parse("refresh");
        fails(ErrorCode.AUTH_TOKEN_INVALID,()->s.refresh(refresh));
    }
 @Test void refreshValidation(){
        Claims c=claims();
        when(c.get("type",String.class)).thenReturn("ACCESS");
        fails(ErrorCode.AUTH_TOKEN_TYPE_MISMATCH,()->s.refresh(refresh));
        when(c.get("type",String.class)).thenReturn("REFRESH");
        fails(ErrorCode.AUTH_ACCOUNT_DISABLED,()->s.refresh(refresh));
        when(r.lockByUsername("user")).thenReturn(Optional.of(a));
        when(c.getExpiration()).thenReturn(Date.from(Instant.now().minusSeconds(2)));
        fails(ErrorCode.AUTH_TOKEN_EXPIRED,()->s.refresh(refresh));
        when(c.getExpiration()).thenReturn(Date.from(Instant.now().plusSeconds(600)));
        TokenBlacklist blocked=new TokenBlacklist();
        blocked.setReason(RevokeReason.LOGOUT);
        when(b.findByJti("jti")).thenReturn(Optional.of(blocked));
        fails(ErrorCode.AUTH_TOKEN_REVOKED,()->s.refresh(refresh));
        blocked.setReason(RevokeReason.REFRESH_ROTATION);
        fails(ErrorCode.AUTH_REFRESH_REUSED,()->s.refresh(refresh));
        verify(rev).revokeAll(1L,RevokeReason.REFRESH_REUSE_DETECTED);
    }
 AuthToken token(String sid){
        AuthToken x=new AuthToken();
        x.setSessionId(sid);
        x.setAccount(a);
        x.setTokenType(TokenType.REFRESH);
        x.setLoginAt(Instant.now());
        x.setExpiresAt(Instant.now().plusSeconds(600));
        return x;
    }
 @Test void refreshRotation(){
        claims();
        when(r.lockByUsername("user")).thenReturn(Optional.of(a));
        a.setEnabled(false);
        fails(ErrorCode.AUTH_ACCOUNT_DISABLED,()->s.refresh(refresh));
        a.setEnabled(true);
        fails(ErrorCode.AUTH_TOKEN_INVALID,()->s.refresh(refresh));
        AuthToken x=token("sid");
        when(t.findByJti("jti")).thenReturn(Optional.of(x));
        x.setRevoked(true);
        fails(ErrorCode.AUTH_TOKEN_REVOKED,()->s.refresh(refresh));
        x.setRevoked(false);
        x.setTokenType(TokenType.ACCESS);
        fails(ErrorCode.AUTH_TOKEN_REVOKED,()->s.refresh(refresh));
        x.setTokenType(TokenType.REFRESH);
        Account other=new Account();
        other.setId(2L);
        x.setAccount(other);
        fails(ErrorCode.AUTH_TOKEN_REVOKED,()->s.refresh(refresh));
        x.setAccount(a);
        x.setSessionId("other");
        fails(ErrorCode.AUTH_TOKEN_REVOKED,()->s.refresh(refresh));
        x.setSessionId("sid");
        when(issuer.issuePair(a,"sid",x.getLoginAt())).thenReturn(response);
        assertSame(response,s.refresh(refresh));
        verify(rev).revokeSession("sid",RevokeReason.REFRESH_ROTATION);
    }
 @Test void sessionsAndLogout(){
        fails(ErrorCode.ACCOUNT_NOT_FOUND,()->s.logout(p));
        when(r.lockById(1L)).thenReturn(Optional.of(a));
        s.logout(p);
        verify(rev).revokeSession("sid",RevokeReason.LOGOUT);
        s.logoutAll(p);
        verify(rev).revokeAll(1L,RevokeReason.LOGOUT_ALL);
        fails(ErrorCode.ACCOUNT_NOT_FOUND,()->s.me(p));
        when(r.findById(1L)).thenReturn(Optional.of(a));
        assertEquals(1L,s.me(p).id());
        when(t.findActive(eq(1L),eq(TokenType.REFRESH),any(Instant.class))).thenReturn(List.of(token("sid"),token("other")));
        var sessions=s.sessions(p);
        assertTrue(sessions.get(0).current());
        assertFalse(sessions.get(1).current());
        fails(ErrorCode.SESSION_NOT_FOUND,()->s.revoke(p,"missing"));
        s.revoke(p,"other");
        verify(rev).revokeSession("other",RevokeReason.SESSION_REVOKED);
    }
 @Test void password(){
        when(r.lockById(1L)).thenReturn(Optional.of(a));
        ChangePasswordRequest q=new ChangePasswordRequest("old","new");
        fails(ErrorCode.AUTH_OLD_PASSWORD_WRONG,()->s.password(p,q));
        when(enc.matches("old","hash")).thenReturn(true);
        when(enc.matches("new","hash")).thenReturn(true);
        fails(ErrorCode.AUTH_PASSWORD_SAME,()->s.password(p,q));
        when(enc.matches("new","hash")).thenReturn(false);
        when(enc.encode("new")).thenReturn("newhash");
        s.password(p,q);
        assertEquals("newhash",a.getPassword());
        assertNotNull(a.getPasswordChangedAt());
        verify(rev).revokeAll(1L,RevokeReason.PASSWORD_CHANGED);
    }
}

