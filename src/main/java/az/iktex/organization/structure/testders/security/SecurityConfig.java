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
 @Bean public org.springframework.boot.web.servlet.FilterRegistrationBean<az.iktex.organization.structure.testders.security.jwt.JwtAuthenticationFilter> jwtFilterRegistration(az.iktex.organization.structure.testders.security.jwt.JwtAuthenticationFilter filter){
  var reg=new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);reg.setEnabled(false);return reg;
 }
 @Bean public SecurityFilterChain filterChain(HttpSecurity http, RestAuthenticationEntryPoint entryPoint, RestAccessDeniedHandler denied, az.iktex.organization.structure.testders.security.jwt.JwtAuthenticationFilter jwtFilter) throws Exception {
  // CSRF is disabled because bearer credentials travel in an explicit Authorization header, not automatically sent cookies.
  return http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .exceptionHandling(e->e.authenticationEntryPoint(entryPoint).accessDeniedHandler(denied))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/register","/api/auth/login","/api/auth/refresh","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/error").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/auth/**").authenticated()
    .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/users/**").hasAnyRole("ADMIN","MANAGER")
    .requestMatchers("/api/users/**").hasRole("ADMIN")
    .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/students/**","/api/courses/**","/api/authors/**","/api/books/**","/api/departments/**","/api/employees/**").authenticated()
    .requestMatchers(org.springframework.http.HttpMethod.DELETE,"/api/students/**","/api/courses/**","/api/authors/**","/api/books/**").hasRole("ADMIN")
    .requestMatchers("/api/students/**","/api/courses/**","/api/authors/**","/api/books/**").hasAnyRole("ADMIN","MANAGER")
    .requestMatchers("/api/departments/**","/api/employees/**").hasRole("ADMIN")
    .requestMatchers("/api/cache/**","/api/tx-demo/**","/api/demo/**","/api/propagation/**","/api/isolation/**").hasRole("ADMIN")
    .anyRequest().authenticated()).addFilterBefore(jwtFilter,org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class).build();
 }
 @Bean public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{return config.getAuthenticationManager();}
}
