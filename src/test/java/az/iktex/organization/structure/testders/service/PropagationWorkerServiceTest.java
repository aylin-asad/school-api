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
class PropagationWorkerServiceTest {
 static void fails(ErrorCode code, org.junit.jupiter.api.function.Executable action) {
         assertEquals(code, assertThrows(BaseException.class, action).getErrorCode());
    }
 @Test void writesAuditEntries(){
        JdbcTemplate jdbc=mock(JdbcTemplate.class);
        PropagationWorkerService s=new PropagationWorkerService(jdbc);
        s.required();
        s.requiresNew();
        s.mandatory();
        verify(jdbc).update(anyString(),eq("REQUIRED"),eq("Should ROLLBACK with the outer transaction"));
        verify(jdbc).update(anyString(),eq("REQUIRES_NEW"),eq("Should COMMIT despite the outer transaction rollback"));
        verify(jdbc).update(anyString(),eq("MANDATORY"),eq("Called successfully inside an existing transaction"));
    }
}

