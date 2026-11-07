package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.PropagationDemoService;
import az.iktex.organization.structure.testders.service.PropagationWorkerService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tx-demo")
public class PropagationDemoController {

    private final PropagationDemoService demo;
    private final PropagationWorkerService worker;
    private final JdbcTemplate jdbc;

    public PropagationDemoController(PropagationDemoService demo,
                                     PropagationWorkerService worker,
                                     JdbcTemplate jdbc) {
        this.demo = demo;
        this.worker = worker;
        this.jdbc = jdbc;
    }

    @PostMapping("/rollback")
    public Map<String, Object> rollback() {
        try {
            demo.demonstrateOuterRollback();
            return Map.of("unexpected", "Outer transaction did not fail");
        } catch (IllegalStateException expected) {
            return Map.of(
                "expectedRollback", true,
                "message", expected.getMessage(),
                "expectedDatabaseResult", "REQUIRED rolled back; REQUIRES_NEW committed"
            );
        }
    }

    @PostMapping("/mandatory/outside")
    public Map<String, Object> mandatoryOutside() {
        try {
            worker.mandatory();
            return Map.of("unexpected", "MANDATORY succeeded without a transaction");
        } catch (IllegalTransactionStateException expected) {
            return Map.of("expectedFailure", true, "reason", "MANDATORY requires an existing transaction");
        }
    }

    @PostMapping("/mandatory/inside")
    public Map<String, Object> mandatoryInside() {
        demo.demonstrateMandatoryInside();
        return Map.of("committed", true, "action", "MANDATORY");
    }

    @GetMapping("/audit")
    public List<Map<String, Object>> audit() {
        return jdbc.queryForList(
            "SELECT id, action, status, message FROM audit_logs " +
            "WHERE entity_name = 'TX_DEMO' ORDER BY id DESC LIMIT 30"
        );
    }
}
