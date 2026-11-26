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
class CourseServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 CourseRepository r=mock(CourseRepository.class);
     StudentRepository students=mock(StudentRepository.class);
     CourseMapper m=mock(CourseMapper.class);
     StudentMapper sm=mock(StudentMapper.class);
     CourseService s=new CourseService(r,students,m,sm);
     Course a=new Course();
     CourseRequest q=new CourseRequest("Math",3,"Teacher");
 @Test void crud(){
        var response=mock(CourseResponse.class);
        when(m.toEntity(q)).thenReturn(a);
        when(r.save(a)).thenReturn(a);
        when(m.toResponse(a)).thenReturn(response);
        assertSame(response,s.create(q));
        when(r.existsByTitle(q.title())).thenReturn(true);
        fails(ErrorCode.COURSE_TITLE_USED,()->s.create(q));
        when(r.findById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.get(1L));
        fails(ErrorCode.COURSE_NOT_FOUND,()->s.get(2L));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,a);
        when(r.existsByTitleAndIdNot(q.title(),1L)).thenReturn(true);
        fails(ErrorCode.COURSE_TITLE_USED,()->s.update(1L,q));
        s.all();
        verify(r).findAll();
        when(r.countStudentsForCourse(1L)).thenReturn(2L);
        assertEquals(2L,s.studentCount(1L));
    }
 @Test void relationships(){
        fails(ErrorCode.STUDENT_NOT_FOUND,()->s.byStudent(1L));
        when(students.existsById(1L)).thenReturn(true);
        s.byStudent(1L);
        verify(r).findAllByStudentsId(1L);
        fails(ErrorCode.COURSE_NOT_FOUND,()->s.students(2L));
        fails(ErrorCode.COURSE_NOT_FOUND,()->s.delete(2L));
        Student b=new Student();
        b.addCourse(a);
        when(r.findDetailById(1L)).thenReturn(Optional.of(a));
        s.students(1L);
        verify(sm).toResponseList(List.of(b));
        s.delete(1L);
        assertFalse(b.getCourses().contains(a));
        verify(r).delete(a);
    }
}

