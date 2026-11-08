package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class IsolationWorkerService {

    private final JdbcTemplate jdbc;

    public IsolationWorkerService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record ReadResult(String isolation, int firstRead, int secondRead) {}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReadResult readCommittedTwice(CountDownLatch firstReadDone, CountDownLatch writerCommitted) {
        return readTwice("READ_COMMITTED", firstReadDone, writerCommitted);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReadResult repeatableReadTwice(CountDownLatch firstReadDone, CountDownLatch writerCommitted) {
        return readTwice("REPEATABLE_READ", firstReadDone, writerCommitted);
    }

    private ReadResult readTwice(String isolation, CountDownLatch firstReadDone,
                                 CountDownLatch writerCommitted) {
        int first = balance();
        firstReadDone.countDown();
        await(writerCommitted);
        return new ReadResult(isolation, first, balance());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addFifty() {
        jdbc.update("UPDATE isolation_accounts SET balance = balance + 50 WHERE id = 1");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Integer> unsafeReadThenWrite(CountDownLatch bothRead,
                                                    CountDownLatch allowWrite) {
        int readBalance = balance();
        bothRead.countDown();
        await(allowWrite);
        // Deliberate bug: overwrites the balance based on a stale value.
        jdbc.update("UPDATE isolation_accounts SET balance = ? WHERE id = 1", readBalance + 10);
        return Map.of("balanceRead", readBalance, "valueWritten", readBalance + 10);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void atomicIncrement() {
        // Correct fix: the database increments the current value under its row lock.
        jdbc.update("UPDATE isolation_accounts SET balance = balance + 10 WHERE id = 1");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<String, Object> offCallRepeatableRead(int doctorId,
                                                      CountDownLatch bothRead,
                                                      CountDownLatch allowWrite) {
        return takeOffCall(doctorId, bothRead, allowWrite);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Map<String, Object> offCallSerializable(int doctorId,
                                                   CountDownLatch bothRead,
                                                   CountDownLatch allowWrite) {
        return takeOffCall(doctorId, bothRead, allowWrite);
    }

    private Map<String, Object> takeOffCall(int doctorId,
                                          CountDownLatch bothRead,
                                          CountDownLatch allowWrite) {
        Integer onCall = jdbc.queryForObject(
                "SELECT count(*) FROM isolation_doctors WHERE on_call = true", Integer.class);
        int observed = onCall == null ? 0 : onCall;
        bothRead.countDown();
        await(allowWrite);
        if (observed > 1) {
            // Each transaction updates a DIFFERENT row, creating write skew under RR.
            jdbc.update("UPDATE isolation_doctors SET on_call = false WHERE id = ?", doctorId);
        }
        return Map.of("doctorId", doctorId, "observedOnCall", observed,
                "attemptedToGoOffCall", observed > 1);
    }

    private int balance() {
        Integer value = jdbc.queryForObject(
                "SELECT balance FROM isolation_accounts WHERE id = 1", Integer.class);
        if (value == null) throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        return value;
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }
}
