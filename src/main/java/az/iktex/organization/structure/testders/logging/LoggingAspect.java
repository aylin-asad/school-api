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
        String args = Arrays.toString(Arrays.stream(pjp.getArgs()).map(a -> {
            if (a == null) return "null";
            String n = a.getClass().getSimpleName().toLowerCase();
            return n.contains("password") || n.contains("request") || n.contains("passport") ? "[REDACTED]" : String.valueOf(a);
        }).toArray());
        long start = System.nanoTime();
        if (controller) log.info("--> {} args={}", method, args); else log.debug("--> {} args={}", method, args);
        try {
            Object result = pjp.proceed();
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (controller) log.info("<-- {} ({} ms)", method, ms); else log.debug("<-- {} ({} ms)", method, ms);
            return result;
        } catch (Throwable ex) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (controller) log.info("<x- {} ({} ms) {}: {}", method, ms, ex.getClass().getSimpleName(), ex.getMessage());
            else log.debug("<x- {} ({} ms) {}: {}", method, ms, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}
