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
class DepartmentServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 DepartmentRepository r=mock(DepartmentRepository.class);
     EmployeeRepository e=mock(EmployeeRepository.class);
     DepartmentMapper m=mock(DepartmentMapper.class);
     DepartmentService s=new DepartmentService(r,e,m);
     Department a=new Department();
    DepartmentRequest q=new DepartmentRequest("Science","Baku",null);
 @Test void crud(){
        var response=mock(DepartmentResponse.class);
        when(m.toEntity(q)).thenReturn(a);
        when(r.save(a)).thenReturn(a);
        when(m.toResponse(a)).thenReturn(response);
        assertSame(response,s.create(q));
        when(r.existsByName(q.name())).thenReturn(true);
        fails(ErrorCode.DEPARTMENT_NAME_USED,()->s.create(q));
        when(r.findDetailById(1L)).thenReturn(Optional.of(a));
        when(m.toDetailResponse(a)).thenReturn(response);
        assertSame(response,s.get(1L));
        fails(ErrorCode.DEPARTMENT_NOT_FOUND,()->s.get(2L));
        when(r.findById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,a);
        when(r.existsByNameAndIdNot(q.name(),1L)).thenReturn(true);
        fails(ErrorCode.DEPARTMENT_NAME_USED,()->s.update(1L,q));
        s.all();
        verify(r).findAll();
        s.withoutEmployees();
        verify(r).findAllByEmployeesIsEmpty();
        s.delete(1L);
        verify(r).delete(a);
        when(e.existsByDepartmentId(1L)).thenReturn(true);
        fails(ErrorCode.DEPARTMENT_HAS_EMPLOYEES,()->s.delete(1L));
        fails(ErrorCode.DEPARTMENT_NOT_FOUND,()->s.delete(2L));
    }
}

