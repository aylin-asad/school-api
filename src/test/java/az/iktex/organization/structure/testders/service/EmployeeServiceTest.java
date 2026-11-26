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
class EmployeeServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 EmployeeRepository r=mock(EmployeeRepository.class);
    DepartmentRepository d=mock(DepartmentRepository.class);
    EmployeeMapper m=mock(EmployeeMapper.class);
    EmployeeService s=new EmployeeService(r,d,m);
    Employee a=new Employee();
    Department dep=new Department();
    EmployeeRequest q=new EmployeeRequest("A","B",BigDecimal.TEN,2L);
 @Test void crud(){
        var response=mock(EmployeeResponse.class);
        when(d.findById(2L)).thenReturn(Optional.of(dep));
        when(m.toEntity(q)).thenReturn(a);
        when(r.save(a)).thenReturn(a);
        when(m.toResponse(a)).thenReturn(response);
        assertSame(response,s.create(q));
        assertSame(dep,a.getDepartment());
        when(r.existsByDepartmentIdAndNameIgnoreCaseAndSurnameIgnoreCase(2L,"A","B")).thenReturn(true);
        fails(ErrorCode.EMPLOYEE_ALREADY_EXISTS,()->s.create(q));
        when(r.findDetailById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.get(1L));
        fails(ErrorCode.EMPLOYEE_NOT_FOUND,()->s.get(9L));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,a);
        when(r.existsByDepartmentIdAndNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot(2L,"A","B",1L)).thenReturn(true);
        fails(ErrorCode.EMPLOYEE_ALREADY_EXISTS,()->s.update(1L,q));
        s.delete(1L);
        verify(r).delete(a);
        s.byDepartment(2L);
        verify(r).findAllByDepartmentId(2L);
        fails(ErrorCode.DEPARTMENT_NOT_FOUND,()->s.byDepartment(9L));
        s.averageSalary(2L);
        verify(r).averageSalary(2L);
    }
 @Test void validation(){
        for(BigDecimal salary:Arrays.asList(null,BigDecimal.ZERO)){
            fails(ErrorCode.EMPLOYEE_SALARY_RANGE,()->s.create(new EmployeeRequest("A","B",salary,2L)));
        }fails(ErrorCode.EMPLOYEE_SALARY_RANGE,()->s.salaryRange(null,BigDecimal.TEN));
        fails(ErrorCode.EMPLOYEE_SALARY_RANGE,()->s.salaryRange(BigDecimal.ONE,null));
        fails(ErrorCode.EMPLOYEE_SALARY_RANGE,()->s.salaryRange(BigDecimal.TEN,BigDecimal.ONE));
        s.salaryRange(BigDecimal.ONE,BigDecimal.TEN);
        verify(r).findAllBySalaryBetween(BigDecimal.ONE,BigDecimal.TEN);
    }
}

