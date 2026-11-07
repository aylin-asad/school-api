package az.iktex.organization.structure.testders.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationWorkerService {

    private final JdbcTemplate jdbc;

    public PropagationWorkerService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void required() {
        insert("REQUIRED", "Should ROLLBACK with the outer transaction");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNew() {
        insert("REQUIRES_NEW", "Should COMMIT despite the outer transaction rollback");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatory() {
        insert("MANDATORY", "Called successfully inside an existing transaction");
    }

    private void insert(String action, String message) {
        jdbc.update(
            "INSERT INTO audit_logs (action, entity_name, entity_id, status, message) " +
            "VALUES (?, 'TX_DEMO', NULL, 'SUCCESS', ?)",
            action, message
        );
    }
}
