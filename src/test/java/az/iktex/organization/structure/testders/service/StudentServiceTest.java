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
class StudentServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 StudentRepository r=mock(StudentRepository.class);
     CourseRepository c=mock(CourseRepository.class);
     StudentMapper m=mock(StudentMapper.class);
     StudentService s=new StudentService(r,c,m);
 Student a=new Student();
     Course b=new Course();
     StudentRequest q=new StudentRequest("A","B",20);
 @Test void crud(){
        var response=mock(StudentResponse.class);
         when(m.toEntity(q)).thenReturn(a);
        when(r.save(a)).thenReturn(a);
        when(m.toResponse(a)).thenReturn(response);
        assertSame(response,s.create(q));
        when(r.findDetailById(1L)).thenReturn(Optional.of(a));
        when(m.toDetailResponse(a)).thenReturn(response);
        assertSame(response,s.get(1L));
        fails(ErrorCode.STUDENT_NOT_FOUND,()->s.get(2L));
        when(r.findById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,a);
        s.delete(1L);
        verify(r).delete(a);
        fails(ErrorCode.STUDENT_NOT_FOUND,()->s.delete(2L));
        s.all();
        verify(r).findAll();
        s.withoutCourses();
        verify(r).findAllByCoursesIsEmpty();
    }
 @Test void enrollment(){
        when(r.findById(1L)).thenReturn(Optional.of(a));
        when(c.findById(2L)).thenReturn(Optional.of(b));
        fails(ErrorCode.COURSE_NOT_FOUND,()->s.enroll(1L,3L));
        fails(ErrorCode.COURSE_NOT_FOUND,()->s.unenroll(1L,3L));
        s.enroll(1L,2L);
        assertTrue(a.getCourses().contains(b));
        when(r.existsByIdAndCoursesId(1L,2L)).thenReturn(true);
        fails(ErrorCode.STUDENT_ALREADY_ENROLLED,()->s.enroll(1L,2L));
        s.unenroll(1L,2L);
        assertFalse(a.getCourses().contains(b));
        when(r.existsByIdAndCoursesId(1L,2L)).thenReturn(false);
        fails(ErrorCode.STUDENT_NOT_ENROLLED,()->s.unenroll(1L,2L));
    }
}

