package az.iktex.organization.structure.testders.security;
import az.iktex.organization.structure.testders.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
 private final AccountRepository accounts;
 @Override public UserDetails loadUserByUsername(String login){
  var a=accounts.findByUsernameOrEmail(login,login).orElseThrow(()->new UsernameNotFoundException("Invalid credentials"));
  return User.withUsername(a.getUsername()).password(a.getPassword()).roles(a.getRole().name()).disabled(!a.isEnabled()).accountLocked(a.getLockedUntil()!=null && a.getLockedUntil().isAfter(java.time.Instant.now())).build();
 }
}
