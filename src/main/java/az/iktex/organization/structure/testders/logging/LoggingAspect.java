package az.iktex.organization.structure.testders.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("within(az.iktex.organization.structure.testders.controller..*)")
    public void controllerLayer() {}
    @Pointcut("within(az.iktex.organization.structure.testders.service..*)")
    public void serviceLayer() {}
    @Around("controllerLayer() || serviceLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        boolean controller = pjp.getSignature().getDeclaringTypeName().contains(".controller.");
        String method = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
        // Never stringify arbitrary service/controller arguments: raw tokens and passwords may be nested or plain strings.
        String args = "[***]";
        long start = System.nanoTime();
        if (controller) log.info("--> {} args={}", method, args); else log.debug("--> {} args={}", method, args);
        try {
            Object result = pjp.proceed();
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (controller) log.info("<-- {} ({} ms)", method, ms); else log.debug("<-- {} ({} ms)", method, ms);
            return result;
        } catch (Throwable ex) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (controller) log.info("<x- {} ({} ms) {}", method, ms, ex.getClass().getSimpleName());
            else log.debug("<x- {} ({} ms) {}", method, ms, ex.getClass().getSimpleName());
            throw ex;
        }
    }
}
