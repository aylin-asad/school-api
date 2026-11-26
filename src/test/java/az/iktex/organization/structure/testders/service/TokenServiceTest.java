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
class TokenServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 JwtService jwt=mock(JwtService.class);
    AuthTokenRepository r=mock(AuthTokenRepository.class);
    TokenService s=new TokenService(jwt,r);
 @Test void issuesAndStoresBoth(){
        Account a=new Account();
        Instant now=Instant.parse("2026-01-01T00:00:00Z");
        when(jwt.generate(a,TokenType.ACCESS,"sid")).thenReturn(new IssuedToken("access","a",TokenType.ACCESS,now,now.plusSeconds(900)));
        when(jwt.generate(a,TokenType.REFRESH,"sid")).thenReturn(new IssuedToken("refresh","r",TokenType.REFRESH,now,now.plusSeconds(604800)));
        assertEquals(new AuthResponse("Bearer","access",900,"refresh",604800),s.issuePair(a,"sid",now));
        var c=org.mockito.ArgumentCaptor.forClass(AuthToken.class);
        verify(r,times(2)).save(c.capture());
        assertEquals(List.of("a","r"),c.getAllValues().stream().map(AuthToken::getJti).toList());
        for(AuthToken t:c.getAllValues()){
            assertEquals(now,t.getLoginAt());
            assertEquals("sid",t.getSessionId());
            assertSame(a,t.getAccount());
        }assertEquals("access",s.issuePair(a,"sid").accessToken());
    }
}

