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
class AccountServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 AccountRepository r=mock(AccountRepository.class);
    PasswordEncoder e=mock(PasswordEncoder.class);
    TokenBlacklistService b=mock(TokenBlacklistService.class);
    AccountService s=new AccountService(r,e,b);
    Account a=new Account();
 @Test void register(){
        RegisterRequest q=new RegisterRequest("user","u@example.org","Sample123");
        when(e.encode(q.password())).thenReturn("hash");
        when(r.saveAndFlush(any(Account.class))).thenAnswer(i->i.getArgument(0));
        assertEquals(Role.USER,s.register(q).role());
        var c=org.mockito.ArgumentCaptor.forClass(Account.class);
        verify(r).saveAndFlush(c.capture());
        assertEquals("hash",c.getValue().getPassword());
        when(r.existsByEmail(q.email())).thenReturn(true);
        fails(ErrorCode.ACCOUNT_EMAIL_EXISTS,()->s.register(q));
        when(r.existsByUsername(q.username())).thenReturn(true);
        fails(ErrorCode.ACCOUNT_USERNAME_EXISTS,()->s.register(q));
    }
 @Test void administration(){
        a.setId(1L);
        when(r.lockById(1L)).thenReturn(Optional.of(a));
        when(r.save(a)).thenReturn(a);
        when(r.findAll()).thenReturn(List.of(a));
        when(r.findAllByRole(Role.USER)).thenReturn(List.of(a));
        assertEquals(1,s.list(null).size());
        assertEquals(1,s.list(Role.USER).size());
        assertEquals(Role.MANAGER,s.role(1L,2L,Role.MANAGER).role());
        verify(b).revokeAll(1L,RevokeReason.ADMIN_REVOKE);
        assertFalse(s.disable(1L,2L).enabled());
        verify(b).revokeAll(1L,RevokeReason.ACCOUNT_DISABLED);
        assertTrue(s.enable(1L).enabled());
        s.revokeTokens(1L);
        verify(b,times(2)).revokeAll(1L,RevokeReason.ADMIN_REVOKE);
        fails(ErrorCode.ACCOUNT_SELF_MODIFICATION,()->s.disable(1L,1L));
        fails(ErrorCode.ACCOUNT_NOT_FOUND,()->s.enable(9L));
    }
}

