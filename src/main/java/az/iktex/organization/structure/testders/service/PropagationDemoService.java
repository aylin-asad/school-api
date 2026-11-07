package az.iktex.organization.structure.testders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationDemoService {

    private final PropagationWorkerService worker;

    public PropagationDemoService(PropagationWorkerService worker) {
        this.worker = worker;
    }

    @Transactional
    public void demonstrateOuterRollback() {
        worker.required();
        worker.requiresNew();
        throw new IllegalStateException("Intentional outer transaction rollback for demo");
    }

    @Transactional
    public void demonstrateMandatoryInside() {
        worker.mandatory();
    }
}
