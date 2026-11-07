package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.IsolationWorkerService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/isolation")
public class IsolationDemoController {

    private final IsolationWorkerService worker;
    private final JdbcTemplate jdbc;

    public IsolationDemoController(IsolationWorkerService worker, JdbcTemplate jdbc) {
        this.worker = worker;
        this.jdbc = jdbc;
    }

    // D1: Two reads in one transaction, with a concurrent committed writer in between.
    @GetMapping("/d1")
    public Map<String, Object> d1() throws Exception {
        IsolationWorkerService.ReadResult rc = runReader(false);
        IsolationWorkerService.ReadResult rr = runReader(true);
        return Map.of(
                "demo", "D1 - non-repeatable reads",
                "readCommitted", rc,
                "repeatableRead", rr,
                "expected", "READ_COMMITTED sees 100 then 150; REPEATABLE_READ sees 100 then 100",
                "passed", rc.firstRead() == 100 && rc.secondRead() == 150
                        && rr.firstRead() == 100 && rr.secondRead() == 100);
    }

    private IsolationWorkerService.ReadResult runReader(boolean repeatable) throws Exception {
        resetBalance();
        CountDownLatch firstRead = new CountDownLatch(1);
        CountDownLatch writerCommitted = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<IsolationWorkerService.ReadResult> reader = executor.submit(() ->
                    repeatable ? worker.repeatableReadTwice(firstRead, writerCommitted)
                               : worker.readCommittedTwice(firstRead, writerCommitted));
            await(firstRead);
            Future<?> writer = executor.submit(worker::addFifty);
            try {
                get(writer); // Writer must have committed before reader's second SELECT.
            } finally {
                writerCommitted.countDown();
            }
            return get(reader);
        } finally {
            executor.shutdownNow();
        }
    }

    // D2: Two concurrent read-modify-write operations lose one increment.
    // Compare with a safe atomic SQL increment.
    @GetMapping("/d2")
    public Map<String, Object> d2() throws Exception {
        resetBalance();
        CountDownLatch bothRead = new CountDownLatch(2);
        CountDownLatch allowWrite = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Map<String, Integer>> staleReads;
        try {
            Future<Map<String, Integer>> first = executor.submit(() ->
                    worker.unsafeReadThenWrite(bothRead, allowWrite));
            Future<Map<String, Integer>> second = executor.submit(() ->
                    worker.unsafeReadThenWrite(bothRead, allowWrite));
            await(bothRead);
            allowWrite.countDown();
            staleReads = List.of(get(first), get(second));
        } finally {
            allowWrite.countDown();
            executor.shutdownNow();
        }
        int unsafeFinal = balance();

        resetBalance();
        ExecutorService safeExecutor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = safeExecutor.submit(worker::atomicIncrement);
            Future<?> second = safeExecutor.submit(worker::atomicIncrement);
            get(first);
            get(second);
        } finally {
            safeExecutor.shutdownNow();
        }
        int atomicFinal = balance();
        return Map.of(
                "demo", "D2 - lost update",
                "initialBalance", 100,
                "twoStaleReads", staleReads,
                "unsafeFinalBalance", unsafeFinal,
                "atomicFinalBalance", atomicFinal,
                "expectedWithTwoIncrements", 120,
                "passed", unsafeFinal == 110 && atomicFinal == 120);
    }

    // D3: Under REPEATABLE_READ both doctors can go off call (write skew).
    // Under SERIALIZABLE PostgreSQL rejects at least one overlapping transaction.
    @GetMapping("/d3")
    public Map<String, Object> d3() throws Exception {
        Map<String, Object> rr = runWriteSkew(false);
        Map<String, Object> serializable = runWriteSkew(true);
        int rrCount = (int) rr.get("onCallAfter");
        int serialCount = (int) serializable.get("onCallAfter");
        return Map.of(
                "demo", "D3 - write skew",
                "repeatableRead", rr,
                "serializable", serializable,
                "expected", "REPEATABLE_READ leaves 0 doctors on call; SERIALIZABLE leaves at least 1",
                "passed", rrCount == 0 && serialCount >= 1);
    }

    private Map<String, Object> runWriteSkew(boolean serializable) throws Exception {
        resetDoctors();
        CountDownLatch bothRead = new CountDownLatch(2);
        CountDownLatch allowWrite = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Map<String, Object>> outcomes = new ArrayList<>();
        try {
            Future<Map<String, Object>> first = executor.submit(() ->
                    serializable ? worker.offCallSerializable(1, bothRead, allowWrite)
                                 : worker.offCallRepeatableRead(1, bothRead, allowWrite));
            Future<Map<String, Object>> second = executor.submit(() ->
                    serializable ? worker.offCallSerializable(2, bothRead, allowWrite)
                                 : worker.offCallRepeatableRead(2, bothRead, allowWrite));
            await(bothRead);
            allowWrite.countDown();
            recordOutcome(outcomes, 1, first);
            recordOutcome(outcomes, 2, second);
        } finally {
            allowWrite.countDown();
            executor.shutdownNow();
        }
        int left = onCallCount();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("isolation", serializable ? "SERIALIZABLE" : "REPEATABLE_READ");
        result.put("transactions", outcomes);
        result.put("onCallAfter", left);
        result.put("invariantPreserved", left >= 1);
        return result;
    }

    private void recordOutcome(List<Map<String, Object>> outcomes, int doctorId,
                               Future<Map<String, Object>> result) throws Exception {
        try {
            Map<String, Object> value = get(result);
            Map<String, Object> outcome = new LinkedHashMap<>(value);
            outcome.put("committed", true);
            outcomes.add(outcome);
        } catch (ExecutionException ex) {
            String sqlState = findSqlState(ex.getCause());
            // 40001 = expected serialization failure in PostgreSQL's SERIALIZABLE mode.
            outcomes.add(Map.of("doctorId", doctorId, "committed", false,
                    "sqlState", sqlState == null ? "unknown" : sqlState,
                    "error", "Transaction rolled back (expected for SERIALIZABLE if SQLSTATE=40001)"));
        }
    }

    private static String findSqlState(Throwable exception) {
        for (Throwable e = exception; e != null; e = e.getCause()) {
            if (e instanceof SQLException sql && sql.getSQLState() != null) {
                return sql.getSQLState();
            }
        }
        return null;
    }

    private void resetBalance() {
        jdbc.update("UPDATE isolation_accounts SET balance = 100 WHERE id = 1");
    }

    private int balance() {
        return jdbc.queryForObject("SELECT balance FROM isolation_accounts WHERE id = 1", Integer.class);
    }

    private void resetDoctors() {
        jdbc.update("UPDATE isolation_doctors SET on_call = true WHERE id IN (1, 2)");
    }

    private int onCallCount() {
        return jdbc.queryForObject(
                "SELECT count(*) FROM isolation_doctors WHERE on_call = true", Integer.class);
    }

    private static void await(CountDownLatch latch) throws Exception {
        if (!latch.await(30, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timed out coordinating isolation demo threads");
        }
    }

    private static <T> T get(Future<T> future) throws Exception {
        return future.get(30, TimeUnit.SECONDS);
    }
}
