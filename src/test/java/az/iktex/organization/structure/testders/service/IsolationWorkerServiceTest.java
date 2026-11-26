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
class IsolationWorkerServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 JdbcTemplate jdbc=mock(JdbcTemplate.class);
    IsolationWorkerService s=new IsolationWorkerService(jdbc);
    String balance="SELECT balance FROM isolation_accounts WHERE id = 1";
    String count="SELECT count(*) FROM isolation_doctors WHERE on_call = true";
 CountDownLatch ready(){
        return new CountDownLatch(0);
    }
 @Test void readsAndWrites(){
        when(jdbc.queryForObject(balance,Integer.class)).thenReturn(100,150,100,100,100);
        assertEquals(new IsolationWorkerService.ReadResult("READ_COMMITTED",100,150),s.readCommittedTwice(new CountDownLatch(1),ready()));
        assertEquals(new IsolationWorkerService.ReadResult("REPEATABLE_READ",100,100),s.repeatableReadTwice(ready(),ready()));
        s.addFifty();
        verify(jdbc).update("UPDATE isolation_accounts SET balance = balance + 50 WHERE id = 1");
        assertEquals(110,s.unsafeReadThenWrite(ready(),ready()).get("valueWritten"));
        verify(jdbc).update("UPDATE isolation_accounts SET balance = ? WHERE id = 1",110);
        s.atomicIncrement();
        verify(jdbc).update("UPDATE isolation_accounts SET balance = balance + 10 WHERE id = 1");
    }
 @Test void doctors(){
        when(jdbc.queryForObject(count,Integer.class)).thenReturn(2,1,null);
        assertEquals(true,s.offCallRepeatableRead(1,ready(),ready()).get("attemptedToGoOffCall"));
        verify(jdbc).update("UPDATE isolation_doctors SET on_call = false WHERE id = ?",1);
        assertEquals(false,s.offCallSerializable(2,ready(),ready()).get("attemptedToGoOffCall"));
        assertEquals(0,s.offCallSerializable(2,ready(),ready()).get("observedOnCall"));
    }
 @Test void failures() throws InterruptedException {
        fails(ErrorCode.INTERNAL_ERROR,()->s.readCommittedTwice(ready(),ready()));
        when(jdbc.queryForObject(balance,Integer.class)).thenReturn(100);
        CountDownLatch timeout=mock(CountDownLatch.class);
        when(timeout.await(30,TimeUnit.SECONDS)).thenReturn(false);
        fails(ErrorCode.INTERNAL_ERROR,()->s.readCommittedTwice(ready(),timeout));
        when(timeout.await(30,TimeUnit.SECONDS)).thenThrow(new InterruptedException());
        try{
            fails(ErrorCode.INTERNAL_ERROR,()->s.readCommittedTwice(ready(),timeout));
            assertTrue(Thread.currentThread().isInterrupted());
        }finally{
            Thread.interrupted();
        }
    }
}

