package az.iktex.organization.structure.testders.security;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.UnauthorizedException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
 private final HandlerExceptionResolver resolver;
 public RestAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){this.resolver=resolver;}
 public void commence(HttpServletRequest req,HttpServletResponse res,AuthenticationException ex){resolver.resolveException(req,res,null,new UnauthorizedException(ErrorCode.AUTH_UNAUTHORIZED));}
}
