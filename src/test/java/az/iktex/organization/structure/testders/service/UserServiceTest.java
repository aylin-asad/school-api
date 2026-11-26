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
class UserServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 UserRepository r=mock(UserRepository.class);
    PassportRepository p=mock(PassportRepository.class);
    UserMapper m=mock(UserMapper.class);
    PassportMapper pm=mock(PassportMapper.class);
    UserService s=new UserService(r,p,m,pm);
    User a=new User();
    UserRequest q=new UserRequest("A","B","a@example.org",Gender.FEMALE);
 @Test void crud(){
        var response=mock(UserResponse.class);
        when(m.toEntity(q)).thenReturn(a);
        when(r.save(a)).thenReturn(a);
        when(m.toResponse(a)).thenReturn(response);
        assertSame(response,s.create(q));
        when(r.existsByEmail(q.email())).thenReturn(true);
        fails(ErrorCode.USER_EMAIL_EXISTS,()->s.create(q));
        when(r.findDetailById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.get(1L));
        fails(ErrorCode.USER_NOT_FOUND,()->s.get(2L));
        when(r.findByEmail(q.email())).thenReturn(Optional.of(a));
        assertSame(response,s.byEmail(q.email()));
        fails(ErrorCode.USER_NOT_FOUND,()->s.byEmail("missing"));
        when(r.findById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,a);
        when(r.existsByEmailAndIdNot(q.email(),1L)).thenReturn(true);
        fails(ErrorCode.USER_EMAIL_USED,()->s.update(1L,q));
        s.all(null);
        verify(r).findAll();
        s.all(Gender.FEMALE);
        verify(r).findAllByGender(Gender.FEMALE);
        s.withoutPassport();
        verify(r).findAllByPassportIsNull();
        s.delete(1L);
        verify(r).delete(a);
        fails(ErrorCode.USER_NOT_FOUND,()->s.delete(2L));
    }
 @Test void passports(){
        when(r.findById(1L)).thenReturn(Optional.of(a));
        PassportRequest request=new PassportRequest("S1",LocalDate.of(2020,1,1),"AZ");
        Passport passport=new Passport();
        var response=mock(PassportResponse.class);
        when(pm.toEntity(request)).thenReturn(passport);
        when(pm.toResponse(passport)).thenReturn(response);
        fails(ErrorCode.PASSPORT_NOT_FOUND,()->s.passport(1L));
        fails(ErrorCode.PASSPORT_NOT_FOUND,()->s.deletePassport(1L));
        when(p.existsBySerialNumber("S1")).thenReturn(true);
        fails(ErrorCode.PASSPORT_SERIAL_EXISTS,()->s.addPassport(1L,request));
        when(p.existsBySerialNumber("S1")).thenReturn(false);
        assertSame(response,s.addPassport(1L,request));
        assertSame(passport,a.getPassport());
        verify(p).save(passport);
        fails(ErrorCode.USER_PASSPORT_EXISTS,()->s.addPassport(1L,request));
        assertSame(response,s.passport(1L));
        s.deletePassport(1L);
        assertNull(a.getPassport());
    }
}

