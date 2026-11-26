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
class BookServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 BookRepository r=mock(BookRepository.class);
    AuthorRepository a=mock(AuthorRepository.class);
    AuthorBookRepository links=mock(AuthorBookRepository.class);
    BookMapper m=mock(BookMapper.class);
    BookService s=new BookService(r,a,links,m);
    Book b=new Book();
    BookRequest q=new BookRequest("Title",BigDecimal.TEN,100);
 @Test void crud(){
        var response=mock(BookResponse.class);
        when(m.toEntity(q)).thenReturn(b);
        when(r.save(b)).thenReturn(b);
        when(m.toResponse(b)).thenReturn(response);
        assertSame(response,s.create(q));
        when(r.existsByTitleIgnoreCase("Title")).thenReturn(true);
        fails(ErrorCode.BOOK_ALREADY_EXISTS,()->s.create(q));
        when(r.findById(1L)).thenReturn(Optional.of(b));
        when(m.toDetailResponse(b)).thenReturn(response);
        assertSame(response,s.get(1L));
        fails(ErrorCode.BOOK_NOT_FOUND,()->s.get(2L));
        assertSame(response,s.update(1L,q));
        verify(m).updateEntity(q,b);
        when(r.existsByTitleIgnoreCaseAndIdNot("Title",1L)).thenReturn(true);
        fails(ErrorCode.BOOK_ALREADY_EXISTS,()->s.update(1L,q));
        s.all(null);
        verify(r).findAll();
        s.all("Title");
        verify(r).findAllByTitleContainingIgnoreCase("Title");
        s.delete(1L);
        verify(r).delete(b);
    }
 @Test void validation(){
        for(BigDecimal price:Arrays.asList(null,BigDecimal.valueOf(-1))){
            fails(ErrorCode.VALIDATION_ERROR,()->s.create(new BookRequest("T",price,10)));
            fails(ErrorCode.VALIDATION_ERROR,()->s.cheaperThan(price));
        }s.cheaperThan(BigDecimal.ZERO);
        verify(r).findAllByPriceLessThanEqualOrderByPriceAsc(BigDecimal.ZERO);
    }
 @Test void relationships(){
        when(r.findById(1L)).thenReturn(Optional.of(b));
        fails(ErrorCode.AUTHOR_NOT_FOUND,()->s.linkAuthor(1L,2L));
        Author author=new Author();
        when(a.findById(2L)).thenReturn(Optional.of(author));
        s.linkAuthor(1L,2L);
        var captor=org.mockito.ArgumentCaptor.forClass(AuthorBook.class);
        verify(links).save(captor.capture());
        assertSame(b,captor.getValue().getBook());
        assertSame(author,captor.getValue().getAuthor());
        when(links.existsByAuthorIdAndBookId(2L,1L)).thenReturn(true);
        fails(ErrorCode.BOOK_AUTHOR_ALREADY_LINKED,()->s.linkAuthor(1L,2L));
        fails(ErrorCode.BOOK_AUTHOR_NOT_LINKED,()->s.unlinkAuthor(1L,2L));
        when(links.deletePair(2L,1L)).thenReturn(1);
        s.unlinkAuthor(1L,2L);
    }
}

