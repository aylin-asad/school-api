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
class TokenBlacklistServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 TokenBlacklistRepository b=mock(TokenBlacklistRepository.class);
    AuthTokenRepository r=mock(AuthTokenRepository.class);
    TokenBlacklistService s=new TokenBlacklistService(b,r);
 AuthToken token(String jti,boolean expired){
        AuthToken t=new AuthToken();
        Account a=new Account();
        a.setId(1L);
        t.setAccount(a);
        t.setJti(jti);
        t.setTokenType(TokenType.ACCESS);
        t.setExpiresAt(Instant.now().plusSeconds(expired?-60:600));
        return t;
    }
 @Test void lookupCachesOnlyPositive(){
        assertFalse(s.isBlacklisted("x"));
        when(b.existsByJti("x")).thenReturn(true);
        assertTrue(s.isBlacklisted("x"));
        assertTrue(s.isBlacklisted("x"));
        verify(b,times(2)).existsByJti("x");
    }
 @Test void revocationAndCommit(){
        AuthToken t=token("a",false);
        TransactionSynchronizationManager.initSynchronization();
        try{
            s.blacklist(t,RevokeReason.LOGOUT);
            assertTrue(t.isRevoked());
            assertNotNull(t.getRevokedAt());
            assertEquals(RevokeReason.LOGOUT,t.getRevokeReason());
            verify(b).insertIfAbsent("a",1L,"ACCESS","LOGOUT",t.getExpiresAt());
            assertFalse(s.isBlacklisted("a"));
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            assertTrue(s.isBlacklisted("a"));
            s.blacklist(t,RevokeReason.LOGOUT);
            verify(r,times(1)).save(t);
        }finally{
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
 @Test void expiredAndBulk(){
        AuthToken a=token("a",true), c=token("c",true);
        when(r.findAllBySessionIdAndRevokedFalse("s")).thenReturn(List.of(a));
        when(r.findAllByAccountIdAndRevokedFalse(1L)).thenReturn(List.of(c));
        s.revokeSession("s",RevokeReason.LOGOUT);
        s.revokeAll(1L,RevokeReason.LOGOUT_ALL);
        assertTrue(a.isRevoked());
        assertTrue(c.isRevoked());
        verifyNoInteractions(b);
    }
}

