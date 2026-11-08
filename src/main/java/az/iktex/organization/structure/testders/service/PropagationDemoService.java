package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
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
        throw new BusinessException(ErrorCode.INTERNAL_ERROR);
    }

    @Transactional
    public void demonstrateMandatoryInside() {
        worker.mandatory();
    }
}
