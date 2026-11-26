package az.iktex.organization.structure.testders.security.jwt;
import az.iktex.organization.structure.testders.enums.*;
import az.iktex.organization.structure.testders.exception.*;
import az.iktex.organization.structure.testders.repository.AccountRepository;
import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import java.util.List;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwtService; private final TokenBlacklistService blacklist; private final AccountRepository accounts; private final HandlerExceptionResolver resolver;
 public JwtAuthenticationFilter(JwtService jwtService,TokenBlacklistService blacklist,AccountRepository accounts,@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){this.jwtService=jwtService;this.blacklist=blacklist;this.accounts=accounts;this.resolver=resolver;}
 @Override protected boolean shouldNotFilter(HttpServletRequest req){String path=req.getServletPath();return path.equals("/api/auth/register")||path.equals("/api/auth/login")||path.equals("/api/auth/refresh")||path.equals("/error")||path.equals("/swagger-ui.html")||path.startsWith("/swagger-ui/")||path.startsWith("/v3/api-docs");}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
  String header=req.getHeader(HttpHeaders.AUTHORIZATION);
  if(header==null||!header.startsWith("Bearer ")){chain.doFilter(req,res);return;}
  try{
   Claims c=jwtService.parse(header.substring(7));
   if(!"ACCESS".equals(c.get("type",String.class)))throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_TYPE_MISMATCH);
   if(blacklist.isBlacklisted(c.getId()))throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_REVOKED);
   var a=accounts.findByUsername(c.getSubject()).filter(x->x.isEnabled()).orElseThrow(()->new ForbiddenException(ErrorCode.AUTH_ACCOUNT_DISABLED));
   var principal=new AuthPrincipal(a.getId(),a.getUsername(),a.getRole(),c.getId(),c.get("sid",String.class));
   var authentication=new UsernamePasswordAuthenticationToken(principal,null,List.of(new SimpleGrantedAuthority("ROLE_"+a.getRole().name())));
   var context=SecurityContextHolder.createEmptyContext();context.setAuthentication(authentication);SecurityContextHolder.setContext(context);
   MDC.put("username",a.getUsername());
  }catch(ExpiredJwtException e){fail(req,res,new UnauthorizedException(ErrorCode.AUTH_TOKEN_EXPIRED));return;}
  catch(JwtException|IllegalArgumentException e){fail(req,res,new UnauthorizedException(ErrorCode.AUTH_TOKEN_INVALID));return;}
  catch(BaseException e){fail(req,res,e);return;}
  try{chain.doFilter(req,res);}finally{MDC.remove("username");}
 }
 private void fail(HttpServletRequest req,HttpServletResponse res,Exception ex){SecurityContextHolder.clearContext();resolver.resolveException(req,res,null,ex);}
}
