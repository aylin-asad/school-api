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
class AuthorServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 AuthorRepository r=mock(AuthorRepository.class);
    BookRepository b=mock(BookRepository.class);
    AuthorMapper m=mock(AuthorMapper.class);
    BookMapper bm=mock(BookMapper.class);
    AuthorService s=new AuthorService(r,b,m,bm);
    Author a=new Author();
    AuthorRequest q=new AuthorRequest("A","B","AZ");
 @Test void crud(){
        var response=mock(AuthorResponse.class);
        when(m.toEntity(q)).thenReturn(a);
        when(r.save(a)).thenReturn(a);
        when(m.toResponse(a)).thenReturn(response);
        assertSame(response,s.create(q));
        when(r.existsByNameIgnoreCaseAndSurnameIgnoreCase("A","B")).thenReturn(true);
        fails(ErrorCode.AUTHOR_ALREADY_EXISTS,()->s.create(q));
        when(r.findById(1L)).thenReturn(Optional.of(a));
        assertSame(response,s.get(1L));
        fails(ErrorCode.AUTHOR_NOT_FOUND,()->s.get(2L));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,a);
        when(r.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndIdNot("A","B",1L)).thenReturn(true);
        fails(ErrorCode.AUTHOR_ALREADY_EXISTS,()->s.update(1L,q));
        s.all(null);
        verify(r).findAll();
        s.all("AZ");
        verify(r).findAllByCountryIgnoreCase("AZ");
        s.withoutBooks();
        verify(r).findAllByAuthorBooksIsEmpty();
        Book book=new Book();
        AuthorBook link=new AuthorBook();
        link.setBook(book);
        a.getAuthorBooks().add(link);
        s.books(1L);
        verify(bm).toResponseList(List.of(book));
        s.delete(1L);
        verify(r).delete(a);
    }
}

