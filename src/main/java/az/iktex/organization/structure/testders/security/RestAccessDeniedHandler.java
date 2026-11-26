package az.iktex.organization.structure.testders.security;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.ForbiddenException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
 private final HandlerExceptionResolver resolver;
 public RestAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){this.resolver=resolver;}
 public void handle(HttpServletRequest req,HttpServletResponse res,AccessDeniedException ex){resolver.resolveException(req,res,null,new ForbiddenException(ErrorCode.AUTH_ACCESS_DENIED));}
}
