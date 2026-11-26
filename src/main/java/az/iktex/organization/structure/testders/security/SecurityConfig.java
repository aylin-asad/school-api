package az.iktex.organization.structure.testders.security;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
 @Bean public SecurityFilterChain filterChain(HttpSecurity http, RestAuthenticationEntryPoint entryPoint, RestAccessDeniedHandler denied) throws Exception {
  // CSRF is disabled because bearer credentials travel in an explicit Authorization header, not automatically sent cookies.
  return http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .exceptionHandling(e->e.authenticationEntryPoint(entryPoint).accessDeniedHandler(denied))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/register","/api/auth/login","/api/auth/refresh","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/error").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated()).build();
 }
 @Bean public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{return config.getAuthenticationManager();}
}
